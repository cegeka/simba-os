package org.simbasecurity.client.data;

public class Tuple<TYPE_1, TYPE_2> {
    private final TYPE_1 headerName;
    private final TYPE_2 header;

    public Tuple(TYPE_1 headerName, TYPE_2 header) {
        this.headerName = headerName;
        this.header = header;
    }

    public TYPE_1 getFirstObject() {
        return headerName;
    }

    public TYPE_2 getSecondObject() {
        return header;
    }
}
