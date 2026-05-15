package com.example.stocklocal.ui.login;

import com.example.stocklocal.data.repository.PreferencesRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class LoginViewModel_Factory implements Factory<LoginViewModel> {
  private final Provider<PreferencesRepository> preferencesRepositoryProvider;

  public LoginViewModel_Factory(Provider<PreferencesRepository> preferencesRepositoryProvider) {
    this.preferencesRepositoryProvider = preferencesRepositoryProvider;
  }

  @Override
  public LoginViewModel get() {
    return newInstance(preferencesRepositoryProvider.get());
  }

  public static LoginViewModel_Factory create(
      Provider<PreferencesRepository> preferencesRepositoryProvider) {
    return new LoginViewModel_Factory(preferencesRepositoryProvider);
  }

  public static LoginViewModel newInstance(PreferencesRepository preferencesRepository) {
    return new LoginViewModel(preferencesRepository);
  }
}
