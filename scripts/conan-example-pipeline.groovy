 node {
        // Clone the code from github:
        git url :'https://github.com/memsharded/example-poco-timer.git'

         // Obtain an Artifactory server instance, defined in Jenkins --> Manage:
        def server = Artifactory.server 'art1'

        // Create a local build-info instance:
        def buildInfo = Artifactory.newBuildInfo()
        buildInfo.name = "conan-example-pipeline"

        // Create a conan client instance:
        def conanClient = Artifactory.newConanClient()

        // Add a new repository named 'conan-local' to the conan client.
        // The 'remote.add' method returns a 'serverName' string, which is used later in the script:
        String serverName = conanClient.remote.add server: server, repo: "conan-virtual"

        // Run a conan build. The 'buildInfo' instance is passed as an argument to the 'run' method:
        conanClient.run(command: "install . --build missing", buildInfo: buildInfo)

        // Create an upload command. The 'serverName' string is used as a conan 'remote', so that
        // the artifacts are uploaded into it:
        String command = "upload * --all -r ${serverName} --confirm"

        // Run the upload command, with the same build-info instance as an argument:
        conanClient.run(command: command, buildInfo: buildInfo)

         // Publish the build-info to Artifactory:
        server.publishBuildInfo buildInfo
}
