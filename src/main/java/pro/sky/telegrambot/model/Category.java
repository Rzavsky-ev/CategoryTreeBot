package pro.sky.telegrambot.model;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Сущность, представляющая узел дерева категорий.
 * Поддерживает иерархическую структуру через связи parent-child.
 */
@Entity
@Table(name = "category_tree")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    /**
     * Родительская категория (null для корневых)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = true)
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> children = new ArrayList<>();

    public Category() {
    }

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
