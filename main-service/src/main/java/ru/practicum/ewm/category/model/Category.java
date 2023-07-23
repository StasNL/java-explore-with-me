package ru.practicum.ewm.category.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Data
@Table(name = "categories")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long catId;
    @Column(name = "category_name")
    private String name;

    @Override
    public String toString() {
        return "Category{" +
                "catId=" + catId +
                ", name='" + name + '\'' +
                '}';
    }
}
