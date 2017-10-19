package nl.knaw.dans.dataverse.bridge.db.dao;

import nl.knaw.dans.dataverse.bridge.db.domain.DvnTdrUser;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

/**
 * Class DvnTdrUserDao
 * Created by Eko Indarto
 * <p>
 * This class is used to access data for the DvnTdrUser entity.
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
public class DvnTdrUserDao {

    // An EntityManager will be automatically injected from entityManagerFactory
    // setup on DatabaseConfig class.
    @PersistenceContext
    private EntityManager entityManager;


    /**
     * Save the dvnTdrUser in the database.
     */
    public void create(DvnTdrUser dvnTdrUser) {
        entityManager.persist(dvnTdrUser);
        return;
    }

    /**
     * Delete the dvnTdrUser from the database.
     */
    public void delete(DvnTdrUser dvnTdrUser) {
        if (entityManager.contains(dvnTdrUser))
            entityManager.remove(dvnTdrUser);
        else
            entityManager.remove(entityManager.merge(dvnTdrUser));
        return;
    }

    /**
     * Return all the dvnTdrUsers stored in the database.
     */
    @SuppressWarnings("unchecked")
    public List<DvnTdrUser> getAll() {
        return entityManager.createQuery("from DvnTdrUser order by id").getResultList();
    }


    /**
     * Update the passed dvnTdrUser in the database.
     */
    public void update(DvnTdrUser dvnTdrUser) {
        entityManager.merge(dvnTdrUser);
        return;
    }

    public DvnTdrUser getById(long id) {
        return entityManager.find(DvnTdrUser.class, id);
    }

    public DvnTdrUser getByDvnUserAndTdrName(String dvnUser, long tdrId) {

        Query query = entityManager.createQuery(
                "from DvnTdrUser where dvnUser = :dvnUser and tdr.id = :tdrId")
                .setParameter("dvnUser", dvnUser)
                .setParameter("tdrId", tdrId);
        try {
            return (DvnTdrUser) query.getSingleResult();
        } catch (NoResultException nre) {
            //Ignore this because as per your logic this is ok!
        }
        return null;
    }
}

