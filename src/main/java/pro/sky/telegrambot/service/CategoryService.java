package pro.sky.telegrambot.service;

import pro.sky.telegrambot.exception.CategoryExistsException;
import pro.sky.telegrambot.exception.CategoryNotFoundException;
import pro.sky.telegrambot.exception.CategoryTreeIsEmptyException;

/**
 * Сервис для работы с иерархией категорий.
 * <p>
 * Предоставляет методы для управления древовидной структурой категорий:
 * <ul>
 *     <li>Добавление корневых и дочерних категорий</li>
 *     <li>Удаление категорий</li>
 *     <li>Получение текстового представления структуры</li>
 * </ul>
 * <p>
 * Сервис обеспечивает целостность иерархии при выполнении операций.
 */
public interface CategoryService {


    /**
     * Добавляет новую корневую категорию.
     *
     * @param name название создаваемой категории
     * @throws CategoryExistsException  если категория с таким именем уже существует
     * @throws IllegalArgumentException если имя категории пустое или null
     */
    void addRootCategory(String name);

    /**
     * Добавляет дочернюю категорию к указанной родительской.
     *
     * @param parentName название родительской категории
     * @param childName  название создаваемой дочерней категории
     * @throws CategoryNotFoundException если родительская категория не найдена
     * @throws CategoryExistsException   если категория с таким именем уже существует
     * @throws IllegalArgumentException  если любое из имен пустое или null
     */
    void addChildCategory(String parentName, String childName);

    /**
     * Удаляет категорию по имени.
     * <p>
     * При удалении родительской категории также удаляются все её дочерние категории.
     *
     * @param name название категории для удаления
     * @throws CategoryNotFoundException если категория не найдена
     * @throws IllegalArgumentException  если имя пустое или null
     */
    void removeCategory(String name);

    /**
     * Генерирует текстовое представление дерева категорий.
     *
     * @return форматированное строковое представление иерархии категорий
     * @throws CategoryTreeIsEmptyException если дерево категорий пустое
     */
    String viewTree();
}
