![GitHub downloads](https://img.shields.io/github/downloads/jonelo/HashGarten/total?color=green) (direct) ![GitHub downloads](https://img.shields.io/github/downloads/jonelo/jacksum-fbi-windows/total?color=green) (FBI Windows) ![GitHub downloads](https://img.shields.io/github/downloads/jonelo/jacksum-fbi-linux/total?color=green) (FBI Linux) ![GitHub downloads](https://img.shields.io/github/downloads/jonelo/jacksum-fbi-macos/total?color=green) (FBI macOS) 

# HashGarten

HashGarten is a graphical user interface for the desktop (Windows, Linux and macOS) that makes the features of
[Jacksum](https://github.com/jonelo/jacksum) available without a command line. Jacksum is both a command line tool and
a Java library.

HashGarten is also part of the Jacksum File Browser Integration:
- https://github.com/jonelo/jacksum-for-windows
- https://github.com/jonelo/jacksum-for-macos
- https://github.com/jonelo/jacksum-for-linux

## Trivia

In a kindergarten, children are meant to be cherished and cared for like young plants. The German word has survived to
this day and has spread to many other languages. HashGarten borrows the idea: it is a garden, too, but one for hash
algorithms.

## Screenshots

### About

![About](https://github.com/user-attachments/assets/1e595e3a-da81-4900-a185-526089dae8f9)


### Find and Select Hash Algorithms

![Find and Select Hash Algorithms](https://github.com/user-attachments/assets/b8e2668e-e4cb-4d14-b68c-c195af97aa5d)

The list of algorithms in the screenshot has been filtered by a regex. HashGarten currently supports 586 hash algorithms, plus 492
HMAC variants.

### Calculate Hash Values

![Calculate Hash Values](https://github.com/user-attachments/assets/7bab63a5-adbc-4312-8f05-a5cf989fdc81)

### Verify Hash Values

![Verify Hash Values](https://github.com/user-attachments/assets/4fc1c957-2537-4179-a176-2c048df9fb86)

### Interactive

![Interactive](https://github.com/user-attachments/assets/db02b411-b089-45e0-9032-f41b5cbc804d)

### Settings

![Settings](https://github.com/user-attachments/assets/737f27d8-d8d2-41a6-b9e4-3c50e1702948)


## Features

- Cross platform without the need of compilation, it runs on Microsoft Windows, Apple macOS, and GNU/Linux
- Run it standalone, or use it from your file browser
- Drag and drop files and directories onto the window
- Calculate and verify hash values of files
- Hash text or other input interactively, as you type
- Start it with Jacksum's command line options, since it supports the same options as Jacksum
- Find the right algorithm quickly; the search field accepts regular expressions (e.g. `^(hmac:)?sha-?\d`)
- Select one or more algorithms out of 586 hash algorithms and 492 HMAC variants
- Get detailed information about each algorithm
- Get context help for many options
- Controls are shown only when you need them
- Multi-screen support: the window opens on the screen where the mouse cursor is
- Keep the window always on top if you like
- Light and dark themes

## Requirements

Java 25 or later.

## Usage

```sh
java -jar HashGarten-0.21.0.jar
java -jar HashGarten-0.21.0.jar -a sha3-256 /path/to/file
```

## Internals

- HashGarten is written entirely in Java and uses Swing.
- It uses Jacksum as a library and calls its API; it never runs the Jacksum command line tool.
- It uses [FlatLaf](https://github.com/JFormDesigner/FlatLaf) for a modern look and feel.
- It accepts the same options as Jacksum, so it can be preconfigured from the command line.
- It stores its settings in `$HOME/.HashGarten.properties`.

## License

HashGarten is free software, licensed under the GNU General Public License, version 3 or (at your option) any later
version. See [LICENSE](LICENSE).
