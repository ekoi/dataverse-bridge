package nl.knaw.dans.dataverse.bridge.db.dao;

import nl.knaw.dans.dataverse.bridge.db.domain.Tdr;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

/**
 * Class TdrDao
 * Created by Eko Indarto
 * <p>
 * This class is used to access data for the Tdr entity.
 * Tdr annotation allows the component scanning support to find and
 * configure the DAO wihtout any XML configuration and also provide the Spring
 * exceptiom translation.
 * Since we've setup setPackagesToScan and transaction manager on
 * DatabaseConfig, any bean method annotated with Transactional will cause
 * Spring to magically call begin() and commit() at the start/end of the
 * method. If exception occurs it will also call rollback().
 */
@Repository
@Transactional
public class TdrDao {

    // An EntityManager will be automatically injected from entityManagerFactory
    // setup on DatabaseConfig class.
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Save the tdr in the database.
     */
    public void create(Tdr tdr) {
        entityManager.persist(tdr);
        return;
    }

    /**
     * Delete the tdr from the database.
     */
    public void delete(Tdr tdr) {
        if (entityManager.contains(tdr))
            entityManager.remove(tdr);
        else
            entityManager.remove(entityManager.merge(tdr));
        return;
    }

    /**
     * Return all the tdrs stored in the database.
     */
    @SuppressWarnings("unchecked")
    public List<Tdr> getAll() {
        return entityManager.createQuery("from Tdr order by id").getResultList();
    }


    /**
     * Update the passed tdr in the database.
     */
    public void update(Tdr tdr) {
        entityManager.merge(tdr);
        return;
    }


    /**
     * Return the Tdr having the passed name.
     */
    public Tdr getByName(String name) {
        Query q = entityManager.createQuery(
                "from Tdr where name = :name")
                .setParameter("name", name);
        try {
            return (Tdr) q.getSingleResult();
        } catch (NoResultException nre) {
            //Ignore this because as per our logic this is ok!
        }
        return null;
    }
} 
