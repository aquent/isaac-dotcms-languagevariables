ISAAC's Language Variables Plugin
======

This repo tracks our custom changes to the ISAAC Language Variables Plugin

More Info Can be found [here](http://geekyplugins.com/dot-cms/language-variables.dot)
For dotCMS Version:  2.5.1 and Newer

Changes:
-----
* Our Configuration is hardcoded (Maybe someday make them host fields)
* I changed it to look at SYSTEM_HOST and the Current Host when looking for Variables
* Added the Gradle build file so it can be built with: gradle osgijar
