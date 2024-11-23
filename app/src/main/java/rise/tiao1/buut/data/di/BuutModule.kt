package rise.tiao1.buut.data.di

import UnsafeOkHttpClient
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.room.Room
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.storage.CredentialsManager
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import com.google.rpc.context.AttributeContext.Auth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import rise.tiao1.buut.data.local.BuutDb
import rise.tiao1.buut.data.local.booking.BookingDao
import rise.tiao1.buut.data.local.notification.NotificationDao
import rise.tiao1.buut.data.local.user.UserDao
import rise.tiao1.buut.data.remote.booking.BookingApiService
import rise.tiao1.buut.data.remote.notification.NotificationApiService
import rise.tiao1.buut.data.remote.user.UserApiService
import rise.tiao1.buut.utils.SharedPreferencesKeys
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Dagger/Hilt Module providing dependencies related to the Irrigation Zone feature.
 * This includes Room database, Retrofit setup, and other related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object BuutModule {
    private var baseUrl: String = "https://10.0.2.2:5001/"

    /**
     * Provides the [BuutDao] for Room database operations.
     */
    @Provides
    fun provideUserDao(database: BuutDb): UserDao {
        return database.userDao
    }

    @Provides
    fun provideBookingDao(database: BuutDb): BookingDao {
        return database.bookingDao
    }

    @Provides
    fun provideNotificationDao(database: BuutDb): NotificationDao {
        return database.notificationDao
    }


    /**
     * Provides the Room database instance using Dagger/Hilt.
     */
    @Singleton
    @Provides
    fun provideRoomDatabase(
        @ApplicationContext appContext: Context)
    : BuutDb {
        return Room.databaseBuilder(
            appContext,
            BuutDb::class.java,
            "db_buut"
        ).fallbackToDestructiveMigration().build()
    }

    /**
     * Provides the [Retrofit] for API calls.
     */
    @Provides
    @Singleton
    fun provideRetrofit(authInterceptor: AuthInterceptor): Retrofit {
        // Logger aanmaken
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }

        // voor productie aanpassen van unsafe naar gewoon OkHttp
        // unsafe wordt gebruikt voor interactie emulator - lokale db
        val httpClient = UnsafeOkHttpClient().getUnsafeOkHttpClient().newBuilder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClient) // Stel de OkHttpClient in voor Retrofit
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Provides the [UserApiService] for REST calls in the user domain.
     */
    @Provides
    @Singleton
    fun provideUserApiService(retrofit: Retrofit): UserApiService {
        return retrofit.create(UserApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideBookingApiService(retrofit: Retrofit): BookingApiService {
        return retrofit.create(BookingApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideNotificationApiService(retrofit: Retrofit): NotificationApiService {
        return retrofit.create(NotificationApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideAuthenticationAPIClient(@ApplicationContext appContext: Context): AuthenticationAPIClient {
        val auth0 = Auth0(appContext)
        return AuthenticationAPIClient(auth0)
    }

    @Singleton
    @Provides
    fun provideSharedPreferencesStorage(@ApplicationContext appContext: Context): SharedPreferencesStorage {

        return SharedPreferencesStorage(appContext)
    }

    @Singleton
    @Provides
    fun provideCredentialsManager(authentication: AuthenticationAPIClient, storage: SharedPreferencesStorage): CredentialsManager {
        return CredentialsManager(authentication, storage)
    }

    @Provides
    fun provideSharedPreferences(@ApplicationContext appContext: Context): SharedPreferences {
        return appContext.getSharedPreferences(SharedPreferencesKeys.PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    @Provides
    fun provideAuthInterceptor(sharedPreferences: SharedPreferences): AuthInterceptor {
        return AuthInterceptor(sharedPreferences)
    }
}


class AuthInterceptor @Inject constructor(private val sharedPreferences: SharedPreferences) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = sharedPreferences.getString(SharedPreferencesKeys.ACCESSTOKEN, null)
        val request= chain.request()
        val requestWithHeader = request.newBuilder()
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer $accessToken")
            .build()
        return chain.proceed(requestWithHeader)
    }
}
