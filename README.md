# README #

This program allow you to download movie subtitles from http://www.opensubtitles.org in a few easy steps.
For more see [wiki](https://bitbucket.org/rafalmag/subtitlesdownloader/wiki/).

Requires Java 8u40+

## Wiki

Build status
https://ci.appveyor.com/project/rafalmag/subtitlesdownloader/branch/default

This program allow you to download movie subtitles from http://www.opensubtitles.org in a few easy steps:

- Drag and drop (or open) movie file

[comment]: <> (01-selectMovie.PNG)

- choose movie title

[comment]: <> (02-selectTitle.PNG)

- choose subtitle

[comment]: <> (03-selectSubtitles.PNG)

- download and test subtitle

[comment]: <> (04-downloadAndTest.PNG)

- after testing please mark subtitles as valid

[comment]: <> (Latest version can be downloaded from here: https://bitbucket.org/rafalmag/subtitlesdownloader/downloads)

## License

[The BSD 2-Clause License](https://web.archive.org/web/20200621210917/http://opensource.org/licenses/BSD-2-Clause)

## TODO

- #22: Change OpenSubtitles API from RPC to REST [deadline: end of 2023]
- #21: Support Java11+
- ~~#20: Make all communication with OpenSubtitles over https~~
- #19: Fix preferences storage
- #18: Speedup startup
- #17: Add support for NapiProjekt database
- #16: Add support for Napisy24 database

## Build tips

To run in Intellij add to VM arguments
```
--module-path C:\Users\Rafal\.jdks\openjfx-20_windows-x64_bin-sdk\javafx-sdk-20\lib --add-modules javafx.controls,javafx.fxml
```

To run via gradle:
```
gradlew run
```

