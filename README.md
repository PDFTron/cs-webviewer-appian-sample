# cs-webviewer-appian-sample
Connected System for Appian WebViewer Component. You can install it directly from [Appian Marketplace](https://community.appian.com/b/appmarket/posts/pdftron-web-viewer-connected-system).

## How to install
The Component Plug-in and the Connected System Plug-in are packaged in two separate bundles. Installing both requires creating both bundles and then placing both into the plug-in directory separately

#### Installing the Connected System Plug-in
* Enter the `connectedSystemPlugin` directory
* Run the gradle JAR task (typically `./gradlew build`)
* Drop the generated jar (which will be located in `build/libs/`) into the plugin directory of your Appian install `<AE_ROOT>/_admin/plugins`

#### Installing the Component Plug-in
* See [WebViewer Plugin](https://github.com/PDFTron/webviewer-appian-sample) for installation instructions for the Component Plug-in

## How to check for security vulnerablities

Run `gradlew dependencyCheckAnalyze`. Afterwards, update `build.gradle`. Build the package. Run vulnerability check again.