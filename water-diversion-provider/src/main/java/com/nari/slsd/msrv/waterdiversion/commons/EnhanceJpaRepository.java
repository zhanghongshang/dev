package com.nari.slsd.msrv.waterdiversion.commons;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.Iterator;


@SuppressWarnings("SpringJavaConstructorAutowiringInspection")
public class EnhanceJpaRepository<T, ID extends Serializable>  extends SimpleJpaRepository<T, ID> implements JpaRepositoryEnhance<T, ID> {

    private static final int BATCH_SIZE = 1000;

    private final JpaEntityInformation<T, ?> entityInformation;
    private final EntityManager em;
    public EnhanceJpaRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation,entityManager);
        this.entityInformation = entityInformation;
        this.em = entityManager;
    }

    public EnhanceJpaRepository(Class<T> domainClass, EntityManager em) {
        this(JpaEntityInformationSupport.getEntityInformation(domainClass, em), em);
    }


    @Transactional
    @Override
    public <S extends T> Iterable<S> batchSave(Iterable<S> entities) {
        Iterator<S> iterator = entities.iterator();
        int index = 0;
        clear ();
        while (iterator.hasNext()){
            em.persist(iterator.next());
            index++;
            if (index % BATCH_SIZE == 0){
                flush();
                clear ();
            }
        }
        if (index % BATCH_SIZE != 0){
            flush();
            clear ();
        }
        return entities;
    }

    @Transactional
    @Override
    public <S extends T> Iterable<S> batchUpdate(Iterable<S> entities) {
        Iterator<S> iterator = entities.iterator();
        int index = 0;
        while (iterator.hasNext()){
            em.merge(iterator.next());
            index++;
            if (index % BATCH_SIZE == 0){
                flush();
                clear ();
            }
        }
        if (index % BATCH_SIZE != 0){
            flush();
            clear ();
        }
        return entities;
    }

    @Transactional
    public void clear() {
        em.clear ();
    }

    @Transactional
    public void flush() {
        em.flush ();
    }

}
