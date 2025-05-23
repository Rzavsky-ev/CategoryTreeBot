package pro.sky.telegrambot.model;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Класс-сущность, представляющий категорию в древовидной структуре.
 * <p>
 * Категория может иметь родительскую категорию и список дочерних категорий,
 * что позволяет строить иерархические структуры любой глубины.
 * <p>
 * Связи между категориями:
 * <ul>
 *     <li>Многие-к-одному с родительской категорией (ManyToOne)</li>
 *     <li>Один-ко-многим с дочерними категориями (OneToMany)</li>
 * </ul>
 *
 * @see Entity Аннотация, указывающая, что класс является JPA сущностью
 * @see Table Аннотация для указания имени таблицы в БД
 */
@Entity
@Table(name = "category_tree")
public class Category {

    /**
     * Уникальный идентификатор категории
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Название категории (уникальное, обязательное поле)
     */
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    /**
     * Родительская категория (может быть null для корневых категорий)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_parent", nullable = true)
    private Category parent;

    /**
     * Список дочерних категорий
     */
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> children = new ArrayList<>();

    /**
     * Конструктор по умолчанию (требуется JPA)
     */
    public Category() {
    }

    /**
     * Конструктор с именем категории
     *
     * @param name название создаваемой категории
     */
    public Category(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Category getParent() {
        return parent;
    }

    public List<Category> getChildren() {
        return children;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Category other)) {
            return false;
        }
        return (Objects.equals(id, other.id) && this.name.equals(other.name));
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", parentId=" + (parent != null ? parent.getId() : "null") +
                '}';
    }
}
