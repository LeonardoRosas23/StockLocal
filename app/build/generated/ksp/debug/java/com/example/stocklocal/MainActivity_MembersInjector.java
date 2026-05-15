package com.example.stocklocal;

import com.example.stocklocal.data.repository.PreferencesRepository;
import com.example.stocklocal.di.TokenProvider;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<PreferencesRepository> preferencesRepositoryProvider;

  private final Provider<TokenProvider> tokenProvider;

  public MainActivity_MembersInjector(Provider<PreferencesRepository> preferencesRepositoryProvider,
      Provider<TokenProvider> tokenProvider) {
    this.preferencesRepositoryProvider = preferencesRepositoryProvider;
    this.tokenProvider = tokenProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<PreferencesRepository> preferencesRepositoryProvider,
      Provider<TokenProvider> tokenProvider) {
    return new MainActivity_MembersInjector(preferencesRepositoryProvider, tokenProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectPreferencesRepository(instance, preferencesRepositoryProvider.get());
    injectTokenProvider(instance, tokenProvider.get());
  }

  @InjectedFieldSignature("com.example.stocklocal.MainActivity.preferencesRepository")
  public static void injectPreferencesRepository(MainActivity instance,
      PreferencesRepository preferencesRepository) {
    instance.preferencesRepository = preferencesRepository;
  }

  @InjectedFieldSignature("com.example.stocklocal.MainActivity.tokenProvider")
  public static void injectTokenProvider(MainActivity instance, TokenProvider tokenProvider) {
    instance.tokenProvider = tokenProvider;
  }
}
