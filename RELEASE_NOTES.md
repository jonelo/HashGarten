# HashGarten Release Notes

The release notes of all released versions are also published at
https://github.com/jonelo/HashGarten/releases - including the hash values of the released jar files.

## HashGarten 0.21, unreleased

- Bug fixes
  - Select algorithms: the implementation details of an HMAC are shown; selecting e.g.
    `hmac:sha-256` failed with "Key must not be null" if no key had been entered, which was
    always the case in the Interactive operating mode with an empty key field
  - Interactive: the output field is no longer cut off with the Nimbus look and feel; the section
    had a fixed height that was too small for Nimbus
  - the heading "Calculation Performance", the header area of the Output Style tab, the label
    "Style:" and the button "Clear" on the Verification tab are no longer cut off; they had fixed
    sizes that were too small; the same goes for the label "Standard output character set:" on
    the Output Files tab, and for the button "..." to select the verification file with the
    system look and feel on macOS
  - Interactive: the output field is read-only, so the hash value can be selected and copied,
    but no longer changed by mistake
  - an incomplete input (e.g. an empty file list, no algorithm, no verification file) is reported
    once; after the message an empty dialog appeared, and an error was printed to the terminal
  - a file that has been given by command line args and is removed from the file list is no
    longer read; Jacksum read the files from the command line in addition to the list
  - with "stay open", the output and error files of a task are closed when the task is over; a
    following task without an output file wrote into the output file of the previous one
  - "Print header" is remembered after a restart, and it is applied when a style is selected as
    well; it was reset at every start, and a style decided on the header on its own (e.g. sfv
    never printed one, full always did)
  - one hashing thread is remembered; it came back as the number of processors
  - the selected algorithm is remembered if a style with a hardcoded algorithm has been used
    (names-only, sizes-and-names, timestamps-and-names, without-hashes); after a restart the
    algorithm was "none", so after switching back to another style the output showed the file
    sizes instead of hash values
  - Select algorithms: Ok keeps the order of the algorithms, which is the order of the columns of
    a combined output, and it keeps algorithms that have no row in the table (e.g. `all`,
    `crc:...`, `hmac:sha-256:128`) instead of dropping them; Reset removes them; an HMAC that is
    given by an alias (e.g. `hmac:sha256`) is ticked
  - Select algorithms: a filter like "Show checked" is applied again when the dialog is opened
    again; it showed the algorithms of the previous selection
  - a style that is not in the list of styles (e.g. an alias like `linux`, or a style file) is
    kept; the style openssl111-dgst has been added to the list; the next task used the default
    style instead
  - a character set that is given by an alias (e.g. `latin1`, `cp1252`) is kept; the next task
    used UTF-8 instead
  - copy, cut and paste work in the text fields for the verification file, the relative path, the
    output file and the error file again, and text can be dragged into them; only files could be
    dropped onto them
  - an unexpected error during a task shows a short message and the window again; the app kept
    running without a window, and the message was a raw stack trace
  - invalid command line args are reported, and HashGarten starts with the remembered settings;
    it failed with a NullPointerException
  - Interactive: a key file that is selected by the "..." button is used right away; the output
    kept showing the hash with the previous key, and a directory could be selected as key file
  - Interactive: no algorithm selected is reported at the algorithm field; the output kept
    showing the hash of the algorithm that was selected before
  - the suggested output filename follows the path that the paths are relativized to after every
    keystroke, and also when the path is pasted or dropped; it lagged one keystroke behind
  - the theme `nimbus` (set in `.HashGarten.properties`) is kept; it was replaced by `light` the
    first time the settings were saved
  - Verify: a line format of the calculation mode no longer causes the warning "Option -F will be
    ignored, because option -c is used"
  - Verify: the filter "Only show files with the status" has the status ERROR for files that
    could not be read; once it had been switched off (e.g. by `--list-filter ok`), unreadable
    files were never reported again, because the GUI had no control for it
  - a custom style whose only change is "no file size" is remembered as custom; it came back as
    the default style, and with combined algorithms (e.g. `sha1+md5`) the file sizes were printed
    again
  - Top, Up, Down and Bottom move all selected lines of the file list, and the lines stay
    selected; only the first selected line was moved
  - View shows the output file, the error file and the verification file in the character set
    that has been selected for it, and without a Byte Order Mark; it always read UTF-8, so e.g. a
    UTF-16 file was shown garbled
  - a missing verification file resp. custom timestamp format is pointed out by a greyed out
    hint in the empty field; a hint text such as "ENTER A VERIFICATION FILE HERE" was written
    into the field and was passed to Jacksum when the button was pressed again; the message about
    the timestamp format also switches to the tab that contains the field
  - Select algorithms: the filter searches the algorithm ids only, regardless of upper and lower
    case; it also searched the hidden descriptions and the check boxes, so e.g. `true` showed the
    ticked algorithms; "Show checked" and "Show unchecked" can be combined with a filter text
  - diagnostic messages are appended to `$HOME/.HashGarten.log` next to the settings; they were
    written to `hashgarten.log` in the working directory, which often was the folder that was
    being hashed, and if that directory was not writable an error was printed to the terminal
  - Help: if no web browser can be opened (e.g. on some Linux desktops), the address is shown
    so that it can be copied; an error was printed to the terminal instead
  - the Jacksum File Browser Integration (`-c relative` with `--path-relative-to-entry 1`)
    proposes the verification file next to the first selected file again, so that the output of
    a calculation is the input of the verification; it proposed a file in the home directory,
    because Jacksum 4.0.1 rejects a verification file that doesn't exist before it resolves the
    path of the first entry, and the Interactive operating mode failed on every keystroke after
    such a start
  - Verify: a task is refused if the standard output file or the standard error file is the
    verification file, also via a relative path or a symbolic link; the verification file was
    overwritten before it was read
  - Verify: the suggested output file is named `.<ALGORITHM>.log`; it was the name of the
    verification file that `-c relative` stands for, so the verification file was overwritten
  - the file dialogs show hidden files, so that e.g. a verification file `.sha256` can be
    selected
  - a key is no longer saved in `.HashGarten.properties`, where it was stored unencrypted; a key
    that has been saved by an older version is removed the next time the settings are saved; the
    name of a key file is still remembered
  - errors in the parameters at startup are written to `$HOME/.HashGarten.log`; they were printed
    to the terminal, which can't be seen if HashGarten has been started from a file browser

- Enhancements
  - Interactive: the output field has a context menu to copy the hash value to the clipboard
  - Input: Add allows to select several files and directories at once, and it starts in the
    directory that has been used last, also after a restart
  - the dialogs to select the verification file and the key file start in the directory that has
    been used last, also after a restart, unless the text field names a file in a directory
  - Output Files: the output file and the error file can be selected by a "..." button; the
    dialog remembers its directory like the others
  - Output Style: the directory for "relativize paths to" can be selected by a "..." button,
    which remembers its directory as well
  - Calculation: the key file of an HMAC can be viewed by a "View" button, as text, or as a hex
    dump if it contains binary data
  - the viewer shows the number of lines, the size, the character set and the time of the last
    modification of the file below the text, and the full path of the file in its title
  - Input: the empty file list shows the greyed out hint "Drag and drop files and directories
    here"
  - Preferences: the look and feel can be selected (FlatLaf, Nimbus or the one of the system),
    and it is applied immediately; the dark theme is available with FlatLaf
  - Output Files and Verify: the fields for the standard output file, the standard error file and
    the verification file have a context menu with suggestions: next to the first input file,
    next to the verification file (for the output of a verification), in the directory the paths
    are relativized to, in the working directory, in the home directory and in the temporary
    directory (on macOS also in the temporary user directory); the output and the error can also
    be set to standard output resp. standard error, and the error file to the output file
  - Interactive: the input field and the key field have a context menu with Cut, Copy, Paste and
    Clear; a hidden input resp. key can't be cut or copied
  - Input: the file list shows the icon of each file and directory, on macOS the one of the
    Finder; an entry that doesn't exist anymore says so in its tooltip
  - Input: the entries of the file list can be reordered by drag and drop, and they have a
    context menu with Top, Up, Down, Bottom and Remove; these functions resp. buttons are greyed
    out when they would have no effect

## HashGarten 0.20, Oct 5, 2026

- Bug fixes
  - the Interactive operating mode is no longer blocked by a parameter error dialog that
    reappeared on every keystroke: HashGarten keeps one long living Jacksum Parameters object and
    validates it again and again, but up to and including Jacksum 4.0.0 a repeated validation fails
    if the Jacksum File Browser Integration has started HashGarten with `--path-relative-to-entry`,
    and it also fails if a path that has been remembered from a previous run does not exist anymore
    (e.g. an unmounted volume); such a validation is retried once with repaired path options now
    (issue #14)
  - the Output Style tab shows the path that `--path-relative-to` has been set to even if Jacksum
    could not resolve it, so that it can be seen and corrected instead of being reported as
    "default" while it is still in effect
  - Interactive: an invalid input is no longer reported by a dialog that reappeared on every
    keystroke, and no longer by painting the output field red resp. white, which did not fit the
    dark theme; the field that has to be corrected - the input, the key or the algorithm - is
    marked with an error outline that follows the current theme, the reason becomes its tooltip,
    and the output field is cleared, so that a hash that belongs to an earlier input can no longer
    be copied by mistake
  - preferences are saved when the window is closed as well: theme, always on top, stay open and
    the window positioning were only written to the properties file after a task had been run, so
    closing the window by the window decoration, by File -> Exit or by the Quit menu on macOS
    threw them away, and in the Interactive operating mode they were never saved at all
  - Verification: the settings of the Integrity Verification File Format are no longer discarded;
    the hidden controls of the Output Style tab overwrote the style, the hash value encoding, the
    file size and the timestamp of the verification file right after they had been read
  - Verification: the controls of a customized verification file format are visible at startup if
    the format of the verification file actually is a customized one
  - the timestamp formats `default-utc` and `iso8601utc` are remembered: they ended up in the
    text field for a customized timestamp format, and the next run then failed with an
    "Illegal pattern character" error
  - unticking "Use alternative implementation(s) if available", unticking the parallel reading
    threads and going back to a single hashing thread sticks now; once such a setting had been
    remembered from an earlier run it could never be switched off again
  - Select algorithms: Cancel keeps the algorithms that have been selected before; both Cancel and
    closing the dialog acted like Ok
  - Select algorithms: an algorithm alias resp. an algorithm id that is not spelled in lower case
    (e.g. `sha1` instead of `sha-1`) is ticked now; no algorithm was ticked at all in that case,
    and pressing Ok then cleared the algorithm selection silently
  - Calculation: the algorithm that Jacksum uses by default (sha3-256) is preselected if neither
    `-a` has been given nor an algorithm has been remembered from an earlier run; the field was
    empty, so a task could not be started before an algorithm had been picked from the list
  - Output Files: an output file is suggested if none has been set, so that the result of a task
    can no longer get lost on standard output, which a GUI user never gets to see
  - the dialog that reports a finished task does not print "null" anymore as the name of the
    output resp. the error log file if no such file has been set, and the viewer is not opened
    for a file that has not been set either
  - an output file that has been typed or dropped in is not overruled anymore when the algorithm
    or the relative path is changed
  - no key is handed over to Jacksum if the key field is empty and none of the selected algorithms
    is an HMAC; an empty key made Jacksum print `-k txt:` to the header of the output file, even
    for algorithms that are not HMACs at all; for an HMAC an empty key is handed over as an empty
    text key (like `-k txt:`), because RFC 2104 allows it and Jacksum would reject the HMAC for
    lacking a key otherwise
  - the key type "Password" is remembered, so that the key field stays masked after a restart
  - the button to select a verification file also works if the text field contains a filename
    without a directory
  - a colon (as in `hmac:sha3-256`) is replaced by an equals sign for the name of the verification
    file that `relative` stands for as well; that was done for the output file only
  - Remove removes all selected lines of the file list, not just the first one
  - Select algorithms: "Show checked" and "Show unchecked" no longer match algorithms that have
    the word "true" resp. "false" in their description
  - diagnostic messages are appended to `hashgarten.log` instead of being printed to the standard
    streams, because those are controlled by Jacksum and can point to the user's own output file
  - Interactive: a key that has no effect, because none of the selected algorithms is an HMAC, is
    marked with a warning outline that follows the current theme, and the reason becomes its
    tooltip; Jacksum ignores such a key silently, so typing it seemed to have no effect on the
    output at all
  - a file list that has been remembered from a previous run is discarded at startup; it usually
    was a temporary file of the Jacksum File Browser Integration that does not exist anymore, which
    caused a parameter error at every start; a file list that is given by command line args is
    still used
  - `mvn package` copies the jacksum and flatlaf jars next to the HashGarten jar, where the
    `Class-Path` of its manifest expects them; `java -jar target/HashGarten-0.20.0.jar` failed with
    a `NoClassDefFoundError` otherwise
  - macOS: the menus "Operating Mode" and "Help" of the screen menu bar are drawn right after the
    start and after the theme has been changed; they sometimes did not appear until the mouse was
    moved over the menu bar; menus and items that move into the application menu are removed
    instead of hidden, the File menu is removed if both Preferences and Quit are in the application
    menu, the menu bar is redrawn once after the window has been activated for the first time, and
    a theme change no longer updates the layered pane and the glass pane
  - macOS 26 and later: the Settings item of the application menu is no longer indented
  - Interactive: the hash of the empty input is shown right away, not only after the first
    keystroke
  - the tooltip of the key field tells whether the key is used, depending on whether an HMAC has
    been selected
  - Save in the file list remembers the list after a restart as well; it was only kept in memory,
    so a removed entry came back at the next start unless a task had been run; a file list that
    is given by `--file-list` still takes precedence over the saved list
  - HashGarten quits immediately; it waited for Java's GUI toolkit to shut itself down, which
    took about two seconds before a terminal accepted input again
  - the option to find Alternate Data Streams (ADS) is unticked and disabled on systems other
    than Windows, because Jacksum scans for ADS on Windows only; ticking it had no effect there

- Enhancements
  - requires Jacksum 4.0.1
  - Traversal Options: added "Find all Unix file types" for `--scan-all-unix-file-types`, which
    also reads block and character devices, named pipes and sockets; it has an effect on
    Unix-like operating systems only and is disabled on Windows
  - the About dialog shows the version and the vendor of the Java runtime, and the version of
    FlatLaf
  - requires Java 25 (was Java 21); the manifest of the jar carries
    `Enable-Native-Access: ALL-UNNAMED`, so that `java -jar` does not warn about native access,
    which is used on macOS for the application menu, and by FlatLaf
  - revised texts of labels, tooltips and messages throughout the GUI: typos have been fixed
    (e.g. "Toglggle", "has ben set"), the wording is consistent ("OK" instead of "Ok",
    "Select Algorithms", "Algorithm ID"), tooltips describe what a control does, and every
    control that had no tooltip has got one

## HashGarten 0.19, August 22, 2026

- Algorithm Selection: added all HMAC algorithms that are supported by Jacksum
- Calculation Panel: added user input feature for adjusting HAMC Options (Key Type and Key)
- Output Files Panel: moved Standard output/error log character set to the Output Files Options for improved clarity
- Verification Panel: added options to ignore hashes, sizes and/or timestamp values if they are present in the verification file
- added input type BubbleBabble and z-base-32 because they are also supported for -q

## HashGarten 0.18, October 26, 2024

- fixed issue #8
- added some more context specific help buttons

## HashGarten 0.17.0, April 27, 2024

- Enhancements
  - added improvements for the appearance of the GUI on macOS
  - removed the error code on the cancel action

## HashGarten 0.16.0, April 3, 2024

- Enhancements
  - added a menu, including File, Operating Mode, and Help
  - added Set Preferences, and Exit to the File menu
  - added "Calculate Hash Values", and "Verify Hash Values" to the "Operating Mode" menu
  - added "HashGarten Homepage", "Report Issue for Hashgarten", "Jacksum Manpage", "Jacksum Homepage", "Report Isssue for Jacksum", and About to the Help menu
  - added the alternative implementation option to the Calculation tab
  - added tooltips with the name of the algorithm to the algorithm id colum at the dialog "Select algorithms"
  - verification tab is now invisible in calculation mode (before that it was just greyed out)
  - added a drop handler to the file textfields
  - added a view button to see the content of the verification file
  - added the Integrity Verificaiton File Format to the Verification tab
  - added context help buttons to many user components
  - made the "Stay the window open after a task has been finished" the default
  - added the option "After starting, center the window on the screen where the mouse cursor is"
  - Fontend tab has been moved to the Preferences
  - renamed installation folder from "Jacksum Windows Explorer Integration" to "Jacksum Windows File Explorer Integration".

## HashGarten 0.15.0, March 3, 2024

- Bug fixes
  - issue #2 - Cannot run HashGarten GUI standalone (on Debian with fvwm due to wrong calculation of GUI coords)

- Enhancements
  - requires Jacksum 3.7.0, and FlatLaF 3.4
  - issue #3 - Add an option to the GUI that allows the GUI to stay open after the task has been finished
  - issue #4 - Add an option to the GUI to disable window always on top
  - issue #5 - HashGarten should work on portrait mode screens (e.g. 1200x1920) without the need to resize the main window
  - added context help for all items at the "Output Style" tab
  - added view buttons for the "Output Style" tab to view output files with the GUI
  - added option to configure a customized path separator

## HashGarten 0.13.0, April 16, 2023 (pre-release)

- works with Jacksum 3.6.0

## HashGarten 0.12.0, January 10, 2023 (pre-release)

- works with Jacksum 3.5.0
- bug fix: additional files that you add to the file list by drag and drop and which are not already in the file list provided by the args are ignored
- bug fix from HashGarten 0.11.0: if custom style has been selected, the encoding is always hex instead of the actual previous selected Encoding

## HashGarten 0.10.0, June 19, 2022 (pre-release)

- works with Jacksum 3.4.0
- removed workaround that was required for Jacksum 3.3.0

## HashGarten 0.9.0, June 4, 2022 (pre-release)

- a new GUI to access Jacksum's features/options, primarily for the SendTo feature at your file browser

---

Note: there are no release notes for 0.11.0 and 0.14.0. Both versions have never been released
publicly - 0.11.0 is only referenced by the release notes of 0.12.0, and 0.14.0 only exists as a
commit in the repository.
