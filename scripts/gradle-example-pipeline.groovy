node {
    def server = Artifactory.newServer url: SERVER_URL, credentialsId: CREDENTIALS
    def rtGradle = Artifactory.newGradleBuild()
    def buildInfo

    stage ('Clone') {
        git url: 'https://github.com/JFrog/project-examples.git'
    }

    stage ('Artifactory configuration') {
        rtGradle.tool = GRADLE_TOOL // Tool name from Jenkins configuration
        rtGradle.deployer repo: 'libs-release-local', server: server
        rtGradle.resolver repo: 'jcenter', server: server
    }

    stage ('Exec Gradle') {
        buildInfo = rtGradle.run rootDir: "gradle-examples/gradle-example-ci-server/", buildFile: 'build.gradle', tasks: 'clean artifactoryPublish'
    }

    stage ('Publish build info') {
        server.publishBuildInfo buildInfo
    }
}
