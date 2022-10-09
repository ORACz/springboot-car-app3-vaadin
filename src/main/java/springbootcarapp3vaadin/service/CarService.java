package springbootcarapp3vaadin.service;

import springbootcarapp3vaadin.model.Car;
import springbootcarapp3vaadin.model.Color;

import java.util.List;
import java.util.Optional;

public interface CarService {

    List<Car> getAll();

    Optional<Car> carById (Long carId);

    List<Car> carByColor (String color);

   boolean save(Car car);

   boolean changeCar(Car changedCar );

   boolean changeColor(Long carId, Color color);

   boolean changeBrand(Long carId, String newBrand);

   boolean removeById (Long carId);

   long findMaxId();

}
