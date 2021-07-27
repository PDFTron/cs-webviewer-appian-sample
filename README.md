# cs-webviewer-appian-sample
Connected System for Appian WebViewer Component

## How to install
The Component Plug-in and the Connected System Plug-in are packaged in two separate bundles. Installing both requires creating both bundles and then placing both into the plug-in directory separately

#### Installing the Connected System Plug-in
* Enter the `connectedSystemPlugin` directory
* Run the gradle JAR task (typically `./gradlew build`)
* Drop the generated jar (which will be located in `build/libs/`) into the plugin directory of your Appian install `<AE_ROOT>/_admin/plugins`

#### Installing the Component Plug-in
* See [WebViewer Plugin](https://github.com/PDFTron/webviewer-appian-sample) for installation instructions for the Component Plug-in
