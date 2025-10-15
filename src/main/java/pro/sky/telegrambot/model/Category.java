package pro.sky.telegrambot.model;

import lombok.Data;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс-сущность, представляющий категорию в древовидной структуре.
 */
@Data
@Entity
@Table(name = "category_tree")
public class Category {

    public Category() {
    }

    public Category(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_parent")
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Category> children = new ArrayList<>();
}
