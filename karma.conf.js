// an example karma.conf.js
module.exports = function(config) {
	config.set({
		basePath: '.',
		frameworks: ['jasmine'],

		browsers: [
            'Chrome',
            //'Firefox', // Firefox is slow!
            //'Safari'
        ],
		reporters: ['progress', 'junit', 'coverage', 'osx'],
		singleRun: false,
        autoWatch : true,

		coverageReporter: {
			type: 'lcovonly',
			dir: 'target/reports/js/coverage/'
		},

		junitReporter: {
			outputFile: 'target/reports/js/karma-test-results.xml',
            suite: 'unit'
		},
		
		files: [
            // Required libraries
            'grails-app/assets/bower_components/jquery/jquery.js',
            'grails-app/assets/bower_components/angular/angular.js',
            'grails-app/assets/bower_components/angular-resource/angular-resource.js',
            'grails-app/assets/bower_components/ng-table/ng-table.js',

            // App under test
            'grails-app/assets/javascripts/**/!(*Spec).coffee',

            // Mocks
            'test/js/lib/angular/angular-mocks.js',

            // Templates
            'grails-app/assets/javascripts/angular/**/*.html',

            // Finally... tests
            'grails-app/assets/javascripts/**/*Spec.coffee',
            'test/js/**/*.coffee',
            'test/js/unit**/*.js'
		],
		exclude: [
		],

		plugins: [
			'karma-coverage',
			'karma-jasmine',
			'karma-chrome-launcher',
			'karma-firefox-launcher',
			'karma-safari-launcher',
			'karma-junit-reporter',
            'karma-coffee-preprocessor',
            'karma-ng-html2js-preprocessor',
            'karma-osx-reporter'
		],

        preprocessors: {
            '**/*.coffee': ['coffee'],
            '**/*.html': ['ng-html2js']
        },

        coffeePreprocessor: {
            // options passed to the coffee compiler
            options: {
                bare: true,
                sourceMap: false
            },
            // transforming the filenames
            transformPath: function(path) {
                return path.replace(/\.js$/, '.coffee');
            }
        },

        /**
         * Angular template loader.
         * see http://daginge.com/technology/2013/12/14/testing-angular-templates-with-jasmine-and-karma/
         */
        ngHtml2JsPreprocessor: {
            moduleName: 'templates',
            stripPrefix: 'grails-app/assets/javascripts/',
            prependPrefix:'/model_catalogue/assets/'
        }
	});
};
