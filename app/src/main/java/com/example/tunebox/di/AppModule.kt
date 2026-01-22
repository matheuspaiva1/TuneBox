package com.example.tunebox.di

import androidx.room.Room
import com.example.tunebox.data.db.AppDatabase
import com.example.tunebox.data.manager.TokenManager
import com.example.tunebox.data.repository.CommentRepository
import com.example.tunebox.data.repository.LikeRepository
import com.example.tunebox.data.repository.SpotifyAuthRepository
import com.example.tunebox.data.repository.SpotifyRepository
import com.example.tunebox.data.repository.LikesViewModel
import com.example.tunebox.notifications.NotificationManager
import com.example.tunebox.notifications.NotificationScheduler
import com.example.tunebox.screens.CommentViewModel
import com.example.tunebox.screens.ProfileViewModel
import com.example.tunebox.screens.SearchViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "tunebox-db"
        ).fallbackToDestructiveMigration().build()
    }
    
    single { get<AppDatabase>().commentDao() }
    single { get<AppDatabase>().likesDao() }

    single { CommentRepository(get()) }
    single { LikeRepository(get()) }
    single { SpotifyRepository() }
    single { SpotifyAuthRepository() }
    
    single { TokenManager(androidContext()) }
    single { NotificationManager(androidContext()) }
    single { NotificationScheduler(androidContext()) }

    viewModel { (accessToken: String, userId: String) ->
        ProfileViewModel(
            repository = get(),
            accessToken = accessToken,
            commentRepository = get(),
            userId = userId
        )
    }

    viewModel { (userId: String) ->
        LikesViewModel(
            repository = get(),
            userId = userId
        )
    }

    viewModel {
        CommentViewModel(
            repository = get()
        )
    }

    viewModel { (accessToken: String) ->
        SearchViewModel(
            repository = get(),
            accessToken = accessToken
        )
    }
}
