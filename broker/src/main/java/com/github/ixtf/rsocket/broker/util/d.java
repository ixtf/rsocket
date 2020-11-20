package com.github.ixtf.rsocket.broker.util;

import io.rsocket.RSocket;
import lombok.extern.slf4j.Slf4j;

import java.util.StringJoiner;
import java.util.function.Function;

@Slf4j
public class RSocketIndex implements IndexedMap<Id, RSocket, Tags> {

    private final IndexedMap<Id, RSocket, Tags> indexedMap = new RoaringBitmapIndexedMap<>();

    private final Function<RSocket, RSocket> rSocketTransformer;

    public RSocketIndex(Function<RSocket, RSocket> rSocketTransformer) {
        this.rSocketTransformer = rSocketTransformer;
    }

    public RSocket get(Id key) {
        return indexedMap.get(key);
    }

    public RSocket put(Id key, RSocket value, Tags indexable) {
        log.debug("indexing RSocket for Id {} tags {}", key, indexable);
        return indexedMap.put(key, rSocketTransformer.apply(value), indexable);
    }

    public RSocket remove(Id key) {
        // TODO: call RSocket.dispose();
        return indexedMap.remove(key);
    }

    public int size() {
        return indexedMap.size();
    }

    public boolean isEmpty() {
        return indexedMap.isEmpty();
    }

    public void clear() {
        indexedMap.clear();
    }

    public Collection<RSocket> values() {
        return indexedMap.values();
    }

    public List<RSocket> query(Tags indexable) {
        return indexedMap.query(indexable);
    }

}
