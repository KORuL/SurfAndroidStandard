package ru.surfstudio.android.imageloader_sample

import android.graphics.PorterDuff
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import org.jetbrains.anko.find
import ru.surfstudio.android.imageloader.ImageLoader

class MainActivity : AppCompatActivity() {

    private val IMAGE_URL = "https://www.besthealthmag.ca/wp-content/uploads/sites/16/2012/04/your-g-spot.jpg"

    private lateinit var imageView: ImageView
    private lateinit var transformButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = find(R.id.imageloader_sample_iv)
        transformButton = find(R.id.image_loader_sample_btn)

        var isLoadOriginal = false

        transformButton.setOnClickListener {
            if (isLoadOriginal) loadOriginalImage() else loadTransformedImage()
            isLoadOriginal = !isLoadOriginal
        }

        imageView.post { loadOriginalImage() }

    }

    private fun loadOriginalImage() {
        ImageLoader
                .with(this)
                .crossFade(500)
                .maxWidth(imageView.width / 2)
                .maxHeight(imageView.height / 2)
                .tile()
                .url(IMAGE_URL)
                .mask(true, R.drawable.ic_error_state, PorterDuff.Mode.LIGHTEN)
                .force()
                .preview(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(imageView)
    }

    private fun loadTransformedImage() {
        ImageLoader
                .with(this)
                .crossFade(500)
                .centerCrop()
                .blur(blurDownSampling = 4)
                .url(IMAGE_URL)
                .force()
                .error(R.drawable.ic_launcher_background)
                .into(imageView)
    }
}