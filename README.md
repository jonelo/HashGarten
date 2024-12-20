![GitHub downloads](https://img.shields.io/github/downloads/jonelo/HashGarten/total?color=green) (direct) ![GitHub downloads](https://img.shields.io/github/downloads/jonelo/jacksum-fbi-windows/total?color=green) (FBI Windows) ![GitHub downloads](https://img.shields.io/github/downloads/jonelo/jacksum-fbi-linux/total?color=green) (FBI Linux) ![GitHub downloads](https://img.shields.io/github/downloads/jonelo/jacksum-fbi-macos/total?color=green) (FBI macOS) 

# HashGarten

A simple graphical user interface for the desktop (Windows, Linux, macOS) to access features of [Jacksum](https://github.com/jonelo/jacksum) by the graphical way. Jacksum is both a command line tool and lib. See also https://github.com/jonelo/jacksum.

HashGarten is also part of the Jacksum File Browser Integration. See also
- https://github.com/jonelo/jacksum-for-windows
- https://github.com/jonelo/jacksum-for-macos
- https://github.com/jonelo/jacksum-for-linux

## Trivia

In a "Kindergarten" children should be cherished and cared for like young plants. The German name has persisted to this day and has spread to other countries. In reference to the famous "Kindergarten", the HashGarten is a similar construction, but for hash algorithms.

## Screenshots

### Use Cases

#### Find and Select Hash Algorithms

![Find and Select Hash Algorithms](https://github.com/user-attachments/assets/41e54f70-591a-4a94-b38c-6cf25282d17e)

Note that the algorithm list above has been filtered. Actually more than 480 algorithms are supported!

#### Calculate Hash Values

![Calculate Hash Values](https://github.com/user-attachments/assets/49f76e03-7821-4a24-ad3e-54ad69caa3f2)

#### Verify Hash Values

![Verify Hash Values](https://github.com/user-attachments/assets/537ddd9d-d202-4f1f-be95-0605b04d8492)


#### Set Preferences

![Set Preferences](https://github.com/user-attachments/assets/042b68bc-0767-4dc4-bd55-7977b722d2c5)


## Features

- Run it standalone or integrate it to your file browser
- Drag and drop files and directories to the GUI
- Calculate and verify hash values
- Initialize the GUI by Jacksum command line options, because it supports the same options as Jacksum does 
- Find suitable algorithms, regular expressions are supported (e.g. `^sha\d?-`)
- Select one or many of a pool of more than 480 algorithms
- Get detailed information about each algorithm
- Get detailed help for many configuration options
- Components become visible if yout need them
- Multi screen environment support (the window appears on the screen on which your mouse cursor is)
- Window is always on top if you want
- Light and Dark themes are supported

## Internals

- HashGarten is written entirely in Java and it uses the Swing framework
- It uses Jacksum as a lib and calls its API, it does not call Jacksum by the command line
- It requires FlatLaF to get a modern look and feel. See also https://github.com/JFormDesigner/FlatLaf
- The GUI supports the same program options as Jacksum does, so you can initialize the GUI even by the command line
- It reads and stores GUI properties from/to `$HOME/.HashGarten.properties`
