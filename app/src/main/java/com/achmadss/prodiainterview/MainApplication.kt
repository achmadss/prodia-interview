package com.achmadss.prodiainterview

import android.app.Application
import android.content.Context
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.util.DebugLogger
import com.achmadss.prodiainterview.data.common.Constants
import com.achmadss.prodiainterview.data.services.provideArticleService
import com.achmadss.prodiainterview.ui.screens.home.HomeScreenViewModel
import com.achmadss.prodiainterview.ui.screens.search.SearchScreenViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

val appModule = module {
    single { provideArticleService() }
    single { androidContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE) }
    viewModel { HomeScreenViewModel(get()) }
    viewModel { SearchScreenViewModel(get(), get()) }
}

class MainApplication: Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            // Log Koin into Android logger
            androidLogger()
            // Reference Android context
            androidContext(this@MainApplication)
            // Load modules
            modules(appModule)
        }
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader(this).newBuilder()
            .crossfade(true)
            .logger(DebugLogger())
            .build()
    }

}