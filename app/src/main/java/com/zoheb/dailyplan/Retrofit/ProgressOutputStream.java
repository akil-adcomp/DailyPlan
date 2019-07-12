package com.zoheb.dailyplan.Retrofit;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Zoheb K Shah on 18/9/18.
 */
class ProgressOutputStream extends OutputStream {
    private final OutputStream stream;
    private final UploadProgressRequestBody.ProgressListener listener;

    private long total;
    private long totalWritten;

    ProgressOutputStream(OutputStream stream, UploadProgressRequestBody.ProgressListener listener, long total) {
        this.stream = stream;
        this.listener = listener;
        this.total = total;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        this.stream.write(b, off, len);
        if (this.total < 0) {
            this.listener.update(-1, -1);
            return;
        }
        if (len < b.length) {
            this.totalWritten += len;
        } else {
            this.totalWritten += b.length;
        }
        this.listener.update(this.totalWritten, this.total);
    }

    @Override
    public void write(int b) throws IOException {
        this.stream.write(b);
        if (this.total < 0) {
            this.listener.update(-1, -1);
            return;
        }
        this.totalWritten++;
        this.listener.update(this.totalWritten, this.total);
    }

    @Override
    public void close() throws IOException {
        if (this.stream != null) {
            this.stream.close();
        }
    }

    @Override
    public void flush() throws IOException {
        if (this.stream != null) {
            this.stream.flush();
        }
    }
}
