package com.tco


import com.tco.user.repository.UserRepository
import com.tco.user.repository.impl.UserRepositoryImpl
import com.tco.security.passwordHasher.PasswordHasher
import com.tco.security.passwordHasher.impl.BCryptPasswordHasher
import com.tco.user.service.AuthService
import com.tco.user.service.UserService
import com.tco.ticket.repository.TicketRepository
import com.tco.ticket.repository.TicketRepositoryImpl
import com.tco.ticket.service.TicketService
import com.tco.ticket.service.TicketServiceImpl
import transaction.repositories.TransactionRepository
import transaction.repositories.TransactionRepositoryImpl
import transaction.services.TransactionService
import transaction.services.TransactionServiceImpl
import io.ktor.server.application.*
import io.ktor.server.plugins.di.*

import com.tco.user.service.impl.AuthServiceImpl
import com.tco.user.service.impl.UserServiceImpl
import event.repositories.EventRepository
import event.repositories.InMemoryEventRepository
import event.services.EventService
import event.services.impl.EventServiceImpl
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactoryOptions
import org.jetbrains.exposed.v1.core.vendors.PostgreSQLDialect
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabaseConfig
import reservation.repositories.InMemoryReservationRepository
import reservation.repositories.ReservationRepository
import reservation.services.ReservationService
import reservation.services.ReservationServiceImpl


// The contents of the `install` function will be used for the project template
fun Application.configureDependencyInjection() {

    // 1. Créez l'instance unique de R2dbcDatabase avant le bloc des dépendances
    val options = ConnectionFactoryOptions.builder()
        .option(ConnectionFactoryOptions.DRIVER, "postgresql")
        .option(ConnectionFactoryOptions.HOST, "localhost")
        .option(ConnectionFactoryOptions.PORT, 5432)
        .option(ConnectionFactoryOptions.USER, "votre_user")
        .option(ConnectionFactoryOptions.PASSWORD, "votre_password")
        .option(ConnectionFactoryOptions.DATABASE, "votre_base")
        .build()

    // 1. Génération d'usine de connexion R2dbc
    val factory = ConnectionFactories.get(options)

// 2. Initialisation correcte avec les paramètres nommés requis
    val r2dbcDatabase = R2dbcDatabase.connect(
        connectionFactory = factory,
        databaseConfig = R2dbcDatabaseConfig {
            // En v1.0+, passer explicitement le dialecte évite les erreurs de résolution de driver
            explicitDialect = PostgreSQLDialect()
        }
    )

    dependencies {
        provide { GreetingService { "Hello, World!" } }

        provide<R2dbcDatabase> { r2dbcDatabase }

        // Repositories
        provide<UserRepository> {
            UserRepositoryImpl(database = resolve<R2dbcDatabase>())
        }
        
        provide<TicketRepository> {
            TicketRepositoryImpl(database = resolve<R2dbcDatabase>())
        }

        provide<TransactionRepository> {
            TransactionRepositoryImpl(database = resolve<R2dbcDatabase>())
        }

        provide<EventRepository> {
            InMemoryEventRepository(database = resolve<R2dbcDatabase>())
        }

        // 4. Si votre ReservationRepository doit aussi parler à la base de données :
        provide<ReservationRepository> {
            InMemoryReservationRepository(database = resolve<R2dbcDatabase>())
        }

        // Services
        provide<AuthService> {
            AuthServiceImpl(
                resolve<UserRepository>(),
                resolve<PasswordHasher>()
            )
        }

        provide<EventService> {
            EventServiceImpl(resolve<EventRepository>()
            )
        }

        provide<UserService> {
            UserServiceImpl(
                resolve<UserRepository>()
            )
        }

        
        provide<TicketService> {
            TicketServiceImpl(
                resolve<TicketRepository>(),
                resolve<ReservationRepository>(),
                resolve<TransactionRepository>()
            )
        }

        provide<TransactionService> {
            TransactionServiceImpl(
                resolve<TransactionRepository>(),
                resolve<ReservationRepository>(),
                resolve<TicketService>()
            )
        }

        provide<ReservationService> {
            ReservationServiceImpl(resolve<ReservationRepository>(), resolve<EventRepository>())
        }

        // Securite
        provide<PasswordHasher> {
            BCryptPasswordHasher()
        }

    }
}
