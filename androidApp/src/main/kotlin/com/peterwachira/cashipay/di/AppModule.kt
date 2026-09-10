package com.peterwachira.cashipay.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.peterwachira.cashipay.BuildConfig
import com.peterwachira.cashipay.data.auth.AuthSessionProvider
import com.peterwachira.cashipay.data.auth.FirebaseAnonymousAuthSessionProvider
import com.peterwachira.cashipay.data.firestore.FirebaseFirestorePaymentDataSource
import com.peterwachira.cashipay.data.firestore.FirestorePaymentDataSource
import com.peterwachira.cashipay.data.remote.KtorPaymentRemoteDataSource
import com.peterwachira.cashipay.data.remote.RemotePaymentDataSource
import com.peterwachira.cashipay.data.repository.AndroidPaymentRepository
import com.peterwachira.cashipay.sharedLogic.data.remote.PaymentApi
import com.peterwachira.cashipay.sharedLogic.domain.repository.PaymentRepository
import com.peterwachira.cashipay.sharedLogic.domain.usecase.ObserveTransactionsUseCase
import com.peterwachira.cashipay.sharedLogic.domain.usecase.SendPaymentUseCase
import org.koin.dsl.module

/**
 * Defines the application-scoped dependency graph for data and domain objects.
 */
internal val appModule = module {
    single {
        FirebaseAuth.getInstance()
    }

    single {
        FirebaseFirestore.getInstance()
    }

    single<AuthSessionProvider> {
        FirebaseAnonymousAuthSessionProvider(
            firebaseAuth = get()
        )
    }

    single<FirestorePaymentDataSource> {
        FirebaseFirestorePaymentDataSource(
            firestore = get(),
            authSessionProvider = get()
        )
    }

    single {
        PaymentApi(
            baseUrl = BuildConfig.PAYMENT_API_BASE_URL
        )
    }

    single<RemotePaymentDataSource> {
        KtorPaymentRemoteDataSource(
            paymentApi = get()
        )
    }

    single<PaymentRepository> {
        AndroidPaymentRepository(
            remotePaymentDataSource = get(),
            firestorePaymentDataSource = get()
        )
    }

    factory {
        SendPaymentUseCase(
            paymentRepository = get()
        )
    }

    factory {
        ObserveTransactionsUseCase(
            paymentRepository = get()
        )
    }
}
