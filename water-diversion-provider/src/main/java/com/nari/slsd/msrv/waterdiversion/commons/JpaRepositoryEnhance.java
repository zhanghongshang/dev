package com.nari.slsd.msrv.waterdiversion.commons;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface JpaRepositoryEnhance<T,ID extends Serializable> extends JpaRepository<T,ID> {

    <S extends T> Iterable<S> batchSave(Iterable<S> entities);
    <S extends T> Iterable<S> batchUpdate(Iterable<S> entities);
}