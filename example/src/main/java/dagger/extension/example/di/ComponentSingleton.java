package dagger.extension.example.di;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Looper;
import android.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.AllowStubGeneration;
import dagger.BindsInstance;
import dagger.Component;
import dagger.Module;
import dagger.Provides;
import dagger.Replaceable;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import dagger.extension.example.R;
import dagger.extension.example.di.qualifier.ApiParam;
import dagger.extension.example.di.qualifier.RxScheduler;
import dagger.extension.example.service.RetrofitWeatherApi;
import dagger.extension.example.service.WeatherService;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static dagger.extension.example.di.qualifier.RxScheduler.Type.MAIN;
import static dagger.extension.example.di.qualifier.RxScheduler.Type.NETWORK;

@Component(modules = {ComponentSingleton.ModuleSingleton.class, AndroidSupportInjectionModule.class})
@Singleton
public interface ComponentSingleton extends AndroidInjector<WeatherApplication>{

    void inject(WeatherApplication application);

    @Component.Builder
    abstract class Builder extends AndroidInjector.Builder<WeatherApplication> {
        @BindsInstance public abstract Builder context(Context context);
        public abstract ComponentSingleton build();
    }

    @Module(includes = ActivityBindingsModule.class)
    abstract class ModuleSingleton {

        @Provides
        @Singleton
        public static LocationManager locationManager(Context context) {
            return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }

        @Provides
        public static SharedPreferences sharedPreferences(Context context) {
            return PreferenceManager.getDefaultSharedPreferences(context);
        }

        @Provides @ApiParam("key") @Replaceable
        public static String apiKey(Context context) {
            return context.getString(R.string.api_key);
        }

        @Provides @ApiParam("lang") @Replaceable
        public static String lang() {
            return "en";
        }

        @Provides @ApiParam("units") @Replaceable
        public static String units() {
            return "metric";
        }

        @Provides @Named("endpointUrl") @Replaceable
        public static String endpointUrl(Context context) {
            return context.getString(R.string.weather_endpoint_url);
        }

        @Provides @Replaceable
        public static RetrofitWeatherApi weatherAPI(@Named("endpointUrl") String url) {
            return new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(url)
                    .client(new OkHttpClient.Builder()
                            .connectTimeout(5, TimeUnit.SECONDS)
                            .readTimeout(5, TimeUnit.SECONDS)
                            .build())
                    .build()
                    .create(RetrofitWeatherApi.class);
        }

        @Provides @Singleton @Replaceable
        public static RequestManager glide(Context context) {
            return Glide.with(context);
        }

        @Provides @RxScheduler(NETWORK) @Singleton
        public static Scheduler networkScheduler() {
            return Schedulers.from(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        @Provides @RxScheduler(MAIN) @Singleton @Replaceable
        public static Scheduler mainScheduler() {
            return AndroidSchedulers.mainThread();
        }

    }

}
