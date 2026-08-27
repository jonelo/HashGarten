# HashGarten Release Notes

The release notes of all released versions are also published at
https://github.com/jonelo/HashGarten/releases - including the hash values of the released jar files.

## HashGarten Next Release TBD

- Bug fixes
  - the Simple Interface operating mode is no longer blocked by a parameter error dialog that
    reappeared on every keystroke: HashGarten keeps one long living Jacksum Parameters object and
    validates it again and again, but up to and including Jacksum 4.0.0 a repeated validation fails
    if the Jacksum File Browser Integration has started HashGarten with `--path-relative-to-entry`,
    and it also fails if a path that has been remembered from a previous run does not exist anymore
    (e.g. an unmounted volume); such a validation is retried once with repaired path options now
    (issue #14)
  - the Output Style tab shows the path that `--path-relative-to` has been set to even if Jacksum
    could not resolve it, so that it can be seen and corrected instead of being reported as
    "default" while it is still in effect

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
  - added the alternative implemenation option to the Calculation tab
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
