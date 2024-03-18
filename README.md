# db-external-command-auth

![Build](https://github.com/liff/db-external-command-auth/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/23933.svg)](https://plugins.jetbrains.com/plugin/23933)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/23933.svg)](https://plugins.jetbrains.com/plugin/23933)

<!-- Plugin description -->
Use an external command to acquire database connection credentials.

Executes an external command and extracts database and username from its output.

The plugin will first attempt to extract the information from output from a [Vault](https://www.vaultproject.io/)-like JSON with the following format:

```json
{
  "data": {
    "username": "username",
    "password": "password"
  }
}  
```

Any extra fields are ignored.

If JSON parsing fails, the output is expected to be password and username separated by a newline:

```
password
username
```

The prefix `username:` is removed from the username, so the following also works:

```
password
Username: username
```
<!-- Plugin description end -->

## Installation

- Using the IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "db-external-command-auth"</kbd> >
  <kbd>Install</kbd>
  
- Manually:

  Download the [latest release](https://github.com/liff/db-external-command-auth/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>


---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
[docs:plugin-description]: https://plugins.jetbrains.com/docs/intellij/plugin-user-experience.html#plugin-description-and-presentation
