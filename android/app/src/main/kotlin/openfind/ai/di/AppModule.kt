package openfind.ai.di

import openfind.ai.data.local.AppDatabase
import openfind.ai.data.local.dao.HistoryDao
import openfind.ai.data.local.dao.SavedDao
import openfind.ai.data.local.dao.WatchlistDao
import openfind.ai.data.repository.DomainRepository
import openfind.ai.data.repository.SettingsRepository
import openfind.ai.data.repository.WatchlistRepository
import openfind.ai.viewmodel.BulkViewModel
import openfind.ai.viewmodel.GeneratorViewModel
import openfind.ai.viewmodel.LibraryViewModel
import openfind.ai.viewmodel.SearchViewModel
import openfind.ai.viewmodel.SettingsViewModel
import openfind.ai.viewmodel.WatchlistViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { AppDatabase.build(androidContext()) }
    single<SavedDao> { get<AppDatabase>().savedDao() }
    single<HistoryDao> { get<AppDatabase>().historyDao() }
    single<WatchlistDao> { get<AppDatabase>().watchlistDao() }

    single { SettingsRepository(androidContext()) }
    single { DomainRepository(get()) }
    single { WatchlistRepository(get()) }

    viewModel { SearchViewModel(androidApplication(), get(), get()) }
    viewModel { BulkViewModel(androidApplication(), get()) }
    viewModel { GeneratorViewModel(get()) }
    viewModel { LibraryViewModel(androidApplication(), get(), get()) }
    viewModel { WatchlistViewModel(get()) }
    viewModel { SettingsViewModel(get(), androidContext()) }
}
