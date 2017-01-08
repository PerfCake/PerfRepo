var gulp           = require('gulp');
var gulpSequence = require('gulp-sequence')
var concat         = require('gulp-concat');
var concatVendor   = require('gulp-concat-vendor');
var uglify         = require('gulp-uglify');
var minify         = require('gulp-minify-css')
var mainBowerFiles = require('main-bower-files');
var inject         = require('gulp-inject');
var series         = require('stream-series');
var flatten = require('gulp-flatten');
var watch = require('gulp-watch');

var vendorScripts;
var vendorStyles;

gulp.task('lib-scripts', function () {
    vendorScripts = gulp.src(mainBowerFiles('**/*.js'),{ base: 'bower_components' })
        .pipe(concatVendor('lib.min.js'))
        .pipe(uglify())
        .pipe(gulp.dest('webapp_content/vendor/js'));
});

gulp.task('lib-styles', function () {
    vendorStyles = gulp.src(mainBowerFiles('**/*.css'), {base: 'bower_components'})
        .pipe(concat('lib.min.css'))
        .pipe(minify())
        .pipe(gulp.dest('webapp_content/vendor/css'));
});

gulp.task('lib-fonts', function() {
    gulp.src('webapp_content/bower_components/**/dist/fonts/*.{ttf,woff,woff2,eof,svg}')
        .pipe(flatten())
        .pipe(gulp.dest('webapp_content/vendor/fonts'));
});

gulp.task('copy-app', function() {
    gulp.src('webapp_content/app/**/*')
        .pipe(gulp.dest('src/main/webapp/app'));
});

gulp.task('copy-vendor', function() {
    gulp.src('webapp_content/vendor/**/*')
        .pipe(gulp.dest('src/main/webapp/vendor'));
});

gulp.task('copy-index', function() {
    gulp.src('webapp_content/index.html')
        .pipe(gulp.dest('src/main/webapp'));
});


gulp.task('index', function () {
    var target = gulp.src("webapp_content/index.html");

    var angularModules = gulp.src(['webapp_content/app/**/*.module.js'], {read: false});
    var sources = gulp.src(['webapp_content/app/**/*.js', '!webapp_content/app/**/*.module.js', 'webapp_content/app/**/*.css'], {read: false});

    return target.pipe(inject(series(vendorScripts, vendorStyles, angularModules, sources), {relative: true}))
        .pipe(gulp.dest('webapp_content'));
});


gulp.task('watcher', function () {
    return watch('webapp_content/app/**/*')
        .pipe(gulp.dest('src/main/webapp/app'));
});

// Default Task
gulp.task('default', gulpSequence('lib-scripts', 'lib-styles', 'lib-fonts', 'index',
    ['copy-app', 'copy-vendor', 'copy-index']));