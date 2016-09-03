'use strict';

import gulp from "gulp";
import babelify from "babelify";
import browserify from "browserify";
import source from "vinyl-source-stream";
import buffer from "vinyl-buffer";
import htmlmin from "gulp-htmlmin";
import image from 'gulp-image';
import sass from "gulp-sass";
import uglify from 'gulp-uglify';
import gulpif from 'gulp-if';
import ffmpeg from 'gulp-fluent-ffmpeg';

const basePath = "public/";

const paths = {
    HTML: 'front-end/*.html',
    IMAGES: 'front-end/resources/img/*',
    AUDIO: 'front-end/resources/sound/*',
    ALL: ['front-end/src/js/*.jsx', 'front-end/src/js/**/*.jsx', 'front-end/index.html'],
    JS: ['front-end/src/js/*.jsx', 'front-end/src/js/**/*.jsx'],
    OUT: 'front-end/js/build.js',
    DEST_BUILD: 'front-end/dist/build',
    DEST: 'front-end/dist',
    ENTRY_POINT: 'front-end/src/js/start.jsx',
    OUT_JS_FILE: basePath + '/js/bundle.js',
    CSS_IN: 'front-end/src/css/**/*.scss',
    OUT_CSS: 'view.css',
    CSS_DEST: basePath + '/css',
    IMAGES_DEST: basePath + '/resources/img/',
    AUDIO_DEST: basePath + '/sound',
    AUDIO_DIR: 'front-end/resources/sound/'
};
const options = {
    browserify: {
        entries: paths.ENTRY_POINT,
        extensions: ['.jsx'],
        debug: true
    }
};

const isProduction = () => {
    return process.env.NODE_ENV === 'production';
}

gulp.task('prod', () => {
    process.env.NODE_ENV = 'production';
});

gulp.task('build:js', () => {
    return browserify(options.browserify)
        .transform(babelify)
        .bundle()
        .on("error", function (err) {
            console.log("Error : " + err.message);
        })
        .pipe(source(paths.OUT_JS_FILE))
        .pipe(buffer())
        .pipe(gulpif(isProduction(), uglify()))
        .pipe(gulp.dest('.'));
});

gulp.task('build:css', () => {
    return gulp.src(paths.CSS_IN)
        .pipe(sass((isProduction())? {style: 'compressed'}: {})
            .on('error', sass.logError))
        .pipe(gulp.dest(paths.CSS_DEST));
});

gulp.task('build:html', () =>
    gulp.src(paths.HTML)
        .pipe(htmlmin({collapseWhitespace: true}))
        .pipe(gulp.dest(basePath))
);

gulp.task('build:images', () => {
    gulp.src(paths.IMAGES)
        .pipe(image())
        .pipe(gulp.dest(paths.IMAGES_DEST));
});

gulp.task('build:audio', () => {
    gulp.src(paths.AUDIO)
        .pipe(gulp.dest(paths.AUDIO_DEST));
});

gulp.task('convert:audio', () => {
    gulp.src(paths.AUDIO)
        .pipe(ffmpeg('mp3', function (cmd) {
            return cmd
                .audioBitrate('128k')
                .audioChannels(2)
                .audioCodec('libmp3lame')
        }))
        .pipe(gulp.dest(paths.AUDIO_DIR));
});

gulp.task('watch', () => {
    gulp.watch('front-end/src/js/**/*.jsx', ['build:js']);
    gulp.watch('front-end/js/css/**/*.scss', ['build:css']);
});

gulp.task('build', ['build:css', 'build:html', 'build:js', 'build:images', 'build:audio']);

gulp.task('build:prod', ['prod', 'build:css', 'build:html', 'build:js', 'build:images', 'build:audio']);

gulp.task('default', ['build:css', 'build:html', 'build:js', 'build:images', 'build:audio', 'watch']);