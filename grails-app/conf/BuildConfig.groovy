grails.servlet.version = "3.0" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

// uncomment (and adjust settings) to fork the JVM to isolate classpaths
grails.project.fork = [
        // configure settings for the test-app JVM, uses the daemon by default
        test: false,
        // configure settings for the run-app JVM
        run: false, //[maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
        // configure settings for the run-war JVM
        war: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
        // configure settings for the Console UI JVM
        console: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256]
]


//grails.plugin.location.'ModelCatalogueCorePlugin' = "../ModelCataloguePlugin/ModelCatalogueCorePlugin"
//grails.plugin.location.'ModelCatalogueElasticSearchPlugin' = "../ModelCataloguePlugin/ModelCatalogueElasticSearchPlugin"

// Grails 2.3 uses Aether by default
grails.project.dependency.resolver = "maven"
grails.project.dependency.resolution = {
			
    // inherit Grails' default dependencies
    inherits("global") {
        // specify dependency exclusions here; for example, uncomment this to disable ehcache:
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve
    legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility

    repositories {
        inherits true // Whether to inherit repository definitions from plugins

        grailsPlugins()
        grailsHome()
        grailsCentral()

        mavenLocal()
        mavenCentral()

        // uncomment these (or add new ones) to enable remote dependency resolution from public Maven repositories
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"

        mavenRepo "http://repo.spring.io/milestone/"
        mavenRepo "http://download.java.net/maven/2/"
        mavenRepo 'http://maven.restlet.org'
        mavenRepo "http://repo.grails.org/grails/core"
        mavenRepo 'http://repo.spring.io/milestone'

        mavenRepo 'http://dl.bintray.com/modelcatalogue-core/ModelCatalogueCorePlugin/'
        //git commmavenRepo 'http://dl.bintray.com/modelcatalogue-core/ModelCatalogueElasticSearchPlugin/'

		// Used for Vlad's fixtures plugin
		mavenRepo 'http://dl.bintray.com/metadata/model-catalogue'
    }

    dependencies {


        // Selenium WebDriver, for use in Geb
        def webDriverVersion = "2.41.0"

        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes
        compile 'com.googlecode.json-simple:json-simple:1.1.1'
        //compile "org.compass-project:compass:2.2.1" // Removed search for 0.1

        compile "org.apache.poi:poi:3.8"
        compile "org.apache.poi:poi-ooxml:3.8"

        // Testing modules
        test "org.gebish:geb-spock:0.9.2"
        test "org.seleniumhq.selenium:selenium-support:${webDriverVersion}"
        test "org.seleniumhq.selenium:selenium-firefox-driver:${webDriverVersion}"
        test "org.seleniumhq.selenium:selenium-chrome-driver:${webDriverVersion}"
        test "org.seleniumhq.selenium:selenium-remote-driver:${webDriverVersion}"

        // Required because of bug in 2.37.0 of WebDriver:
        test "org.apache.httpcomponents:httpclient:4.3.1"
        test("org.seleniumhq.selenium:selenium-htmlunit-driver:${webDriverVersion}") {
            exclude 'xml-apis'
        }

        runtime 'mysql:mysql-connector-java:5.1.22'
		test "org.modelcatalogue:simple-fixtures:0.1.2"
    }

    plugins {
        //compile ":searchable:0.6.5" // removed search for 0.1
        compile ":spring-security-ui:0.2"
        compile ':spring-security-core:1.2.7.4'
        compile ":spring-security-acl:1.1.1"

        compile ":audit-logging:0.5.5.3"
        compile ":jquery-ui:1.10.3"
        compile ":famfamfam:1.0.1"

        compile "org.modelcatalogue.plugins:grails-model-catalogue-core-plugin:0.3.1"
        //compile "org.modelcatalogue.plugins:grails-model-catalogue-elastic-search-plugin:0.2"

        compile ':cache:1.0.1'
        compile ':mail:1.0.1', {
            excludes 'spring-test'
        }

        //as suggested on http://grails.org/2.3.5+Release+Notes to use    build ':tomcat:7.0.50'
        //build ":tomcat:7.0.47"
        build ':tomcat:7.0.52.1'

        test ":geb:0.9.2"

        // Uncomment these (or add new ones) to enable additional resources capabilities
        //runtime ":zipped-resources:1.0"
        //runtime ":cached-resources:1.0"
        runtime ":database-migration:1.4.0"

        //as suggested on http://grails.org/2.3.5+Release+Notes to use  runtime ':hibernate:3.6.10.7'
        //runtime ":hibernate:3.6.10.6"
        runtime ":hibernate:3.6.10.13"
        runtime ":jquery:1.8.3"
        runtime ":resources:1.1.6"

        compile ":asset-pipeline:1.8.8"
        compile ":coffee-asset-pipeline:1.8.0"
        compile ":less-asset-pipeline:1.7.0"

        // The following are dead, we shouldn't use them!
        compile ":csv:0.3.1"

        compile ":excel-export:0.2.0"

		// Code quality bits
		compile ":codenarc:0.20"
		test ":code-coverage:1.2.7"

    }
 }

