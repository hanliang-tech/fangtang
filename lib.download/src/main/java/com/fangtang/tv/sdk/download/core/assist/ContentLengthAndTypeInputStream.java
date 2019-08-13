/**
 * ****************************************************************************
 * Copyright 2013-2014 Sergey Tarasevich
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************************************************************
 */
package com.fangtang.tv.sdk.download.core.assist;

import java.io.InputStream;

public class ContentLengthAndTypeInputStream extends ContentLengthInputStream {

    private final InputStream stream;
    private final int length;
    private final String contentType;
    private final String acceptRanges;

    public ContentLengthAndTypeInputStream(InputStream stream, int length, String contentType, String acceptRange) {
        super(stream, length);
        this.stream = stream;
        this.length = length;
        this.contentType = contentType;
        this.acceptRanges = acceptRange;
    }

    public String getContentType() {
        return contentType;
    }

    public String getAcceptRanges() {
        return acceptRanges;
    }
}