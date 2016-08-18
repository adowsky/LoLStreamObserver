'use strict';

import rest from 'rest';
import defaultRequest from 'rest/interceptor/defaultRequest';
import mime from 'rest/interceptor/mime';
import errorCode from 'rest/interceptor/errorCode';
import appJson from 'rest/mime/type/application/json';
import baseRegistry from 'rest/mime/registry';

var registry = baseRegistry.child();

//registry.register('application/json', appJson);

module.exports = rest
    //.wrap(mime, { registry: registry })
    .wrap(mime, { mime: 'application/json' })
    .wrap(errorCode)
    .wrap(defaultRequest, { headers: { 'Accept': 'application/json' }});