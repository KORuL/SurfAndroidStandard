package ru.surfstudio.android.navigation.sample.app.screen.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_auth.*
import ru.surfstudio.android.navigation.command.fragment.ReplaceHard
import ru.surfstudio.android.navigation.sample.R
import ru.surfstudio.android.navigation.sample.app.App
import ru.surfstudio.android.navigation.sample.app.screen.main.MainRoute

class AuthFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_auth, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        auth_btn.setOnClickListener { App.navigator.execute(ReplaceHard(MainRoute())) }
    }
}