# HashGarten

A simple graphical user interface for the desktop (Windows, Linux, macOS) to access features of Jacksum by the graphical way. Jacksum is both a command line tool and lib. See also https://github.com/jonelo/jacksum.

HashGarten is also part of the Jacksum File Browser Integration. See also https://github.com/jonelo/jacksum-fbi-windows.

## Screenshots

### Dark Theme

#### Find and select algorithms

<img width="100%" height="100%" src="https://raw.githubusercontent.com/jonelo/HashGarten/main/docs/images/HashGarten-0.9.0-select-algorithm.png" alt="HashGarten screenshot" style="vertical-align:top;margin:10px 10px" />

Note that the algorithm list above has been filtered. Actually 472 algorithms are supported!

#### Calculate hashes (advanced mode)

<img width="100%" height="100%" src="https://raw.githubusercontent.com/jonelo/HashGarten/main/docs/images/HashGarten-0.9.0.png" alt="HashGarten screenshot" style="vertical-align:top;margin:10px 10px" />


## Features

- run it standalone or integrate it to your file browser
- find algorithms, regular expressions are supported (e.g. `^sha\d?-`)
- select one or many of a pool of 470 algorithms
- get detailed help for each algorithm
- drag and drop files and directories to the GUI
- components become visible if yout need them
- calculate and verify hash values
- window is always on top
- Light and Dark themes are supported

## Internals

- HashGarten is written entirely in Java and it uses the Swing framework
- it requires FlatLaF to get a modern look and feel. See also https://github.com/JFormDesigner/FlatLaf
- it uses Jacksum as a lib and calls its API, it does not call Jacksum by the command line
- the GUI supports the same program options as Jacksum does, so you can initialize the GUI even by the command line
- it reads and stores GUI properties from/to $HOME/.HashGarten.properties
