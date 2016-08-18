'use strict';

import gulp from "gulp";
import babelify from "babelify";
import browserify from "browserify";
import source from "vinyl-source-stream";
import buffer from "vinyl-buffer";
import htmlmin from "gulp-htmlmin";
import image from 'gulp-image';
import sass from "gulp-sass";

const basePath = "../lolstreamobserver-impl/src/main/webapp";

const paths = {

    HTML: 'src/index.html',
    ALL: ['./src/js/*.jsx', './src/js/**/*.jsx', './index.html'],
    JS: ['./src/js/*.jsx', './src/js/**/*.jsx'],
    OUT: './js/build.js',
    DEST_BUILD: './dist/build',
    DEST: './dist',
    ENTRY_POINT: './src/js/start.jsx',
    OUT_JS_FILE: basePath + '/js/bundle.js',
    CSS_IN: 'src/css/**/*.scss',
    OUT_CSS: 'view.css',
    CSS_DEST: basePath + '/css',
    IMAGES_DEST: basePath + '/resources/img/'
};
const options= {
    browserify : {
        entries: paths.ENTRY_POINT,
        extensions:['.jsx'],
        debug: true
    }
};

gulp.task('build:js', () => {
    return browserify(options.browserify)
        .transform(babelify)
        .bundle()
        .on("error", function (err) { console.log("Error : " + err.message); })
        .pipe(source(paths.OUT_JS_FILE))
        .pipe(buffer())
        //.pipe(uglify())
        .pipe(gulp.dest('.'));
});

gulp.task('build:css', () =>{
   return gulp.src(paths.CSS_IN)
       .pipe(sass().on('error', sass.logError))
       .pipe(gulp.dest(paths.CSS_DEST));
});

gulp.task('build:html', () =>
    gulp.src('./*.html')
    .pipe(htmlmin({collapseWhitespace: true}))
    .pipe(gulp.dest(basePath))
);

gulp.task('build:images', () =>{
    gulp.src('./resources/img/*')
        .pipe(image())
        .pipe(gulp.dest(paths.IMAGES_DEST));
});

gulp.task('watch', () => {
    gulp.watch('js/**/*.jsx', ['build:js']);
    gulp.watch('css/**/*.scss',['build:css']);
});

gulp.task('default', [ 'build:css', 'build:html', 'build:js', 'build:images', 'watch' ]);