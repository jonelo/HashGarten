# HashGarten Release Notes

The release notes of all released versions are also published at
https://github.com/jonelo/HashGarten/releases - including the hash values of the released jar files.

## HashGarten 0.20, August 28, 2026

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
  - no key is handed over to Jacksum if the key field is empty; an empty key made Jacksum print
    `-k txt:` to the header of the output file, even for algorithms that are not HMACs at all
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

- Enhancements
  - requires Jacksum 4.0.1

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
