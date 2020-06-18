# ProjectTracker
An Android app for browsing projects on GitHub. This project was built to explore the [GitHub GraphQL API](https://developer.github.com/v4).
It also demonstrates modern Android development techniques including:
- Kotlin
- ViewModel
- LiveData
- Navigation
- MaterialComponents and transitions
- RecyclerView with mulitple view types
- Dagger Hilt
- Testing with Dagger Hilt

The GraphQL is handled by [Apollo](https://github.com/apollographql/apollo-android)

## Setup

You will have to generate a [GitHub personal access token](https://github.com/settings/tokens) to use this app. Once you have generated the key, add it to your gradle properties as `github_token="INSERT_KEY_HERE"`. This is to ensure you do not check your GitHub key in to git.

## Todo

- Better handle Pull Request cards in Projects
- Tests (Currently can't work out a good way to test Apollo, ideally you could pass in mock responses somehow)
- Properly render the Project row on the Projects fragment
- Issue labels