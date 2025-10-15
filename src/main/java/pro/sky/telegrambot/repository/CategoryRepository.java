package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambot.model.Category;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с категориями в базе данных.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Находит категорию по названию.
     *
     * @param name название категории для поиска
     * @return {@link Optional}, содержащий категорию, если найдена
     */
    Optional<Category> findByName(String name);

    /**
     * Проверяет существование категории с указанным названием.
     *
     * @param name название категории для проверки
     * @return true если категория существует, false в противном случае
     */
    boolean existsByName(String name);

    /**
     * Находит все корневые категории (без родителя).
     *
     * @return список корневых категорий
     */
    List<Category> findAllByParentIsNull();

    /**
     * Находит все категории, чьи названия содержатся в переданном списке.
     *
     * @param names список названий категорий для поиска
     * @return список найденных категорий
     */
    List<Category> findByNameIn(List<String> names);
}
