![GitHub downloads](https://img.shields.io/github/downloads/jonelo/HashGarten/total?color=green) (direct) ![GitHub downloads](https://img.shields.io/github/downloads/jonelo/jacksum-fbi-windows/total?color=green) (FBI Windows) ![GitHub downloads](https://img.shields.io/github/downloads/jonelo/jacksum-fbi-linux/total?color=green) (FBI Linux) ![GitHub downloads](https://img.shields.io/github/downloads/jonelo/jacksum-fbi-macos/total?color=green) (FBI macOS) 

# HashGarten

A simple graphical user interface for the desktop (Windows, Linux, macOS) to access features of Jacksum by the graphical way. Jacksum is both a command line tool and lib. See also https://github.com/jonelo/jacksum.

HashGarten is also part of the Jacksum File Browser Integration. See also
- https://github.com/jonelo/jacksum-fbi-windows
- https://github.com/jonelo/jacksum-fbi-macos
- https://github.com/jonelo/jacksum-fbi-linux

## Trivia

In a "Kindergarten" children should be cherished and cared for like young plants. The German name has persisted to this day and has spread to other countries. In reference to the famous "Kindergarten", the HashGarten is a similar construction, but for hash algorithms.

## Screenshots

### Use Cases

#### Find and select algorithms

<img width="100%" height="100%" src="https://raw.githubusercontent.com/jonelo/HashGarten/main/docs/images/HashGarten-0.9.0-select-algorithm.png" alt="HashGarten: find and select algorithms" style="vertical-align:top;margin:10px 10px" />

Note that the algorithm list above has been filtered. Actually more than 470 algorithms are supported!

#### Calculate hashes (advanced mode)

<img width="100%" height="100%" src="https://raw.githubusercontent.com/jonelo/HashGarten/main/docs/images/HashGarten-0.9.0.png" alt="HashGarten: calculate hashes" style="vertical-align:top;margin:10px 10px" />

#### Verify hash values (simple mode)

<img height="100%" src="https://raw.githubusercontent.com/jonelo/HashGarten/main/docs/images/HashGarten-0.14.0-verify-hash-values.png" alt="HashGarten: verify hash values" style="vertical-align:top;margin:10px 10px" />

## Features

- Run it standalone or integrate it to your file browser
- Find suitable algorithms, regular expressions are supported (e.g. `^sha\d?-`)
- Select one or many of a pool of more than 470 algorithms
- Get detailed help for each algorithm
- Drag and drop files and directories to the GUI
- Components become visible if yout need them
- Calculate and verify hash values
- Window is always on top
- Light and Dark themes are supported

## Internals

- HashGarten is written entirely in Java and it uses the Swing framework
- It uses Jacksum as a lib and calls its API, it does not call Jacksum by the command line
- It requires FlatLaF to get a modern look and feel. See also https://github.com/JFormDesigner/FlatLaf
- The GUI supports the same program options as Jacksum does, so you can initialize the GUI even by the command line
- It reads and stores GUI properties from/to $HOME/.HashGarten.properties
