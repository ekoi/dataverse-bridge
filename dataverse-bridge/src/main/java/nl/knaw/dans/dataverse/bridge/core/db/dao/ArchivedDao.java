package nl.knaw.dans.dataverse.bridge.core.db.dao;

import nl.knaw.dans.dataverse.bridge.core.db.domain.Archived;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

/**
 * Class ArchivedDao
 * Created by Eko Indarto
 * <p>
 * This class is used to access data for the Archived entity.
 * Archived annotation allows the component scanning support to find and
 * configure the DAO wihtout any XML configuration and also provide the Spring
 * exceptiom translation.
 * Since we've setup setPackagesToScan and transaction manager on
 * DatabaseConfig, any bean method annotated with Transactional will cause
 * Spring to magically call begin() and commit() at the start/end of the
 * method. If exception occurs it will also call rollback().
 */
@Repository
@Transactional
public class ArchivedDao {

    // An EntityManager will be automatically injected from entityManagerFactory
    // setup on DatabaseConfig class.
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Save the archived in the database.
     */
    public void create(Archived archived) {
        entityManager.persist(archived);
        return;
    }

    /**
     * Delete the archived from the database.
     */
    public void delete(Archived archived) {
        if (entityManager.contains(archived))
            entityManager.remove(archived);
        else
            entityManager.remove(entityManager.merge(archived));
        return;
    }

    /**
     * Return all the archiveds stored in the database.
     */
    @SuppressWarnings("unchecked")
    public List<Archived> getAll() {
        return entityManager.createQuery("from Archived order by id").getResultList();
    }


    /**
     * Update the passed archived in the database.
     */
    public void update(Archived archived) {
        entityManager.merge(archived);
        return;
    }


    /**
     * Return the Archived having the passed name.
     */
    public Archived getBySrcxmlSrcversionTargetiri(String srcXml, String srcVersion, String targetIri) {
        Query q = entityManager.createQuery(
                "from Archived where srcXml = :srcXml and srcVersion = :srcVersion and targetIri = :targetIri")
                .setParameter("srcXml", srcXml)
                .setParameter("srcVersion", srcVersion)
                .setParameter("targetIri", targetIri);
        try {
            return (Archived) q.getSingleResult();
        } catch (NoResultException nre) {
            //Ignore this because as per our logic this is ok!
        }
        return null;
    }
} 
