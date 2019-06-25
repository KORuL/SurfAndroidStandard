package ru.surfstudio.android.build.tasks.deploy_to_mirror

import org.eclipse.jgit.revwalk.RevCommit
import ru.surfstudio.android.build.exceptions.deploy_to_mirror.RevCommitNotFoundException
import ru.surfstudio.android.build.tasks.deploy_to_mirror.repository.BaseGitRepository
import ru.surfstudio.android.build.tasks.deploy_to_mirror.repository.MirrorRepository
import ru.surfstudio.android.build.tasks.deploy_to_mirror.repository.StandardRepository
import ru.surfstudio.android.build.utils.standardHash

/**
 * Git manager
 */
class MirrorManager(
        private val standardRepository: StandardRepository,
        private val mirrorRepository: MirrorRepository,
        private val standardDepthLimit: Int,
        private val mirrorDepthLimit: Int
) {

    /**
     * Mirror standard repository and mirror repository
     *
     * @param rootCommitHash - top commit for mirroring
     */
    fun mirror(rootCommitHash: String) {
        val standardCommits = standardRepository.getAllCommits(rootCommitHash, standardDepthLimit)

        val rootCommit = standardCommits.find { it.name == rootCommitHash }
                ?: throw RevCommitNotFoundException(rootCommitHash)
        val mirrorCommits: Set<RevCommit> = mirrorRepository.getAllBranches()
                .flatMap { mirrorRepository.getAllCommits(it.objectId.name, mirrorDepthLimit) }
                .filter { it.standardHash.isNotEmpty() }
                .toSet()

        val gitTree = buildGitTree(rootCommit, standardCommits, mirrorCommits)

        gitTree.cut()

        commitChanges(gitTree)
    }

    /**
     * Build GitTree with correct structure
     *
     * @param rootCommit - top commit
     * @param endCommits - end commits
     */
    private fun buildGitTree(
            rootCommit: RevCommit,
            standardCommits: Iterable<RevCommit>,
            mirrorCommits: Iterable<RevCommit>
    ): GitTree {
        val gitTree = GitTree()
        gitTree.setRoot(rootCommit)
        gitTree.setEnds(mirrorCommits)

        val iCommits = mutableSetOf(rootCommit)
        val iParents = mutableSetOf<RevCommit>()

        for (standardCommit in standardCommits) {
            iCommits.forEach { commit ->
                val parentHashes = commit.parents.map { it.name }
                val parents = standardCommits.filter { parentHashes.contains(it.name) }

                gitTree.add(commit, parents)

                iParents.addAll(parents.filter { !mirrorCommits.map { it.standardHash }.contains(it.name) })
            }

            iCommits.clear()
            iCommits.addAll(iParents)
            iParents.clear()

            if (iCommits.isEmpty()) break
        }

        return gitTree
    }

    private fun commitChanges(gitTree: GitTree) {
        val standardStartCommit = gitTree.getStandardStartCommit()
        val mirrorStartCommit = gitTree.getMirrorCommitByStandard(standardStartCommit.name)
        val commits = gitTree.getCommitsWithChanges()

        if (commits.isEmpty()) return

        test()

//        standardRepository.reset(standardStartCommit)
//        standardRepository.checkout(standardStartCommit)
//
//        mirrorRepository.reset(mirrorStartCommit)
//        mirrorRepository.checkout(mirrorStartCommit)
//
//        commits.forEach { commit(it, gitTree) }
    }

    private fun commit(commit: RevCommit, gitTree: GitTree) {
        //checkoutнуться or create
        standardRepository.reset(commit)
        standardRepository.checkout(commit)

    }

    fun test(){
        standardRepository.test()
    }
}