# Book Search App 📚

A professional Android application for discovering and downloading e-books, utilizing the [Gutendex API](https://gutendex.com). This project serves as a showcase of modern Android development, featuring Clean Architecture, Jetpack Compose, and a robust offline-first approach.

## ✨ Features

- **Advanced Search**: Search for books by title or author with real-time results.
- **Filtering**: Filter search results by language to find exactly what you need.
- **Search History**: Automatic saving of recent search queries for quick re-entry.
- **Rich Book Details**: View extensive metadata, including author biographies (where available), multiple languages, subjects, and download statistics.
- **Book Downloading**: Integrated file downloader using Ktor streaming to save books directly to device storage.
- **Library Management**: Dedicated state tracking for downloaded books, allowing users to open or delete files.
- **Offline-First**: Comprehensive caching system using Room database to ensure a seamless experience even without an internet connection.
- **Responsive UI**: Built entirely with Jetpack Compose and Material 3, supporting adaptive layouts and Dark Mode.

## 🛠 Tech Stack & Tools

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
- **Networking**: [Ktor](https://ktor.io/) (Client, Logging, Content Negotiation, Serialization)
- **Dependency Injection**: [Koin](https://insert-koin.io/) (Core, Android, Compose)
- **Database**: [Room](https://developer.android.com/training/data-storage/room) (with support for Many-to-Many relationships and Type Converters)
- **Image Loading**: [Coil](https://coil-kt.github.io/coil/) (with Ktor integration)
- **Navigation**: [Jetpack Navigation Compose](https://developer.android.com/jetpack/compose/navigation) (Type-safe routing)
- **Concurrency**: Kotlin Coroutines & Flow
- **Serialization**: Kotlinx Serialization (JSON)

## 🏗 Architecture

The project is structured following **Clean Architecture** principles to ensure maintainability, scalability, and testability:

- **`core`**: Contains shared components like network utilities (`HttpClientFactory`), generic `Result` and `Error` wrappers, and UI helpers (`UiText`).
- **`book`**:
    - **`domain`**: Pure Kotlin layer containing business models (`Book`, `Author`), Use Cases (`DownloadBookUseCase`, `DeleteBookUseCase`), and Repository interfaces.
    - **`data`**: implementation of Repositories, Network data sources (Ktor), Local database (Room entities and DAOs), and Mappers to transform DTOs/Entities into Domain models.
    - **`presentation`**: MVVM implementation using ViewModels, State-driven UI, and reusable Compose components.
- **`di`**: Centralized Koin modules for dependency management.

## 📦 Project Structure

```text
com.stanislavvelykoivan.booksearch
├── app                 # Entry point, Navigation, and Routes
├── book
│   ├── data            # Network, Database, DTOs, Mappers, Repositories
│   ├── domain          # Models, Use Cases, Repository Interfaces
│   └── presentation    # UI Screens, ViewModels, Compose Components
├── core
│   ├── data            # Common Network & Data utilities
│   ├── domain          # Core Error handling & Result types
│   └── presentation    # Common UI logic (Colors, Themes, Resources)
└── di                  # Dependency Injection modules
```

## 🧪 Testing

The project emphasizes reliability through comprehensive testing:
- **Unit Tests**: Logic verification for ViewModels (`BookSearchViewModelTest`), Mappers (`BookMapperTest`), and File Management (`FileStorageManagerTest`).
- **Tools**: [MockK](https://mockk.io/) for mocking, [Turbine](https://github.com/cashapp/turbine) for testing Flows, and [Truth](https://truth.dev/) for fluent assertions.

## 🚀 Getting Started

1.  Clone the repository.
2.  Open in **Android Studio Ladybug (2024.2.1)** or newer.
3.  Ensure you have the **Kotlin Serialization plugin** enabled.
4.  Build and run the `:app` module.

---

Designed and developed by **Stanislav Velykoivan**.
