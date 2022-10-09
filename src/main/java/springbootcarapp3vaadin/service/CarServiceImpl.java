package springbootcarapp3vaadin.service;

import springbootcarapp3vaadin.model.Car;
import springbootcarapp3vaadin.model.Color;

import java.util.*;

public class CarServiceImpl implements CarService {

    private List<Car> cars;

    public CarServiceImpl() {
        this.cars = new ArrayList<>();
        cars.add(new Car(1L, "Audi", "Q9", Color.BLUE));
        cars.add(new Car(2L, "Ferrari", "929", Color.SILVER));
        cars.add(new Car(3L, "Skoda", "Octavia", Color.RED));
    }

    @Override
    public List<Car> getAll() {
        return cars;
    }

    @Override
    public Optional<Car> carById(Long carId) {
        Optional<Car> findCarById = cars.stream().filter(car -> car.getCarId() == carId).findFirst();
        return findCarById;
    }

    @Override
    public List<Car> carByColor(String color) {
        return null;
    }

    @Override
    public boolean save(Car car) {
        return cars.add(car);
    }

    @Override
    public boolean changeCar(Car changedCar) {
        Optional<Car> findCar = cars.stream().filter(car -> car.getCarId() == changedCar.getCarId()).findFirst();
        if (findCar.isPresent()) {
            Car car = findCar.get();
            car.setBrand(changedCar.getBrand());
            car.setModel(changedCar.getModel());
            car.setColor(changedCar.getColor());
            cars.add(changedCar);
        }
        return false;
    }

    @Override
    public boolean changeColor(Long carId, Color color) {
        Optional<Car> findCar2 = cars.stream().filter(car -> car.getCarId() == carId).findFirst();
        if (findCar2.isPresent()) {
            Car carColor = findCar2.get();
            carColor.setColor(color);
            return true;
        }
        return false;
    }

    @Override
    public boolean changeBrand(Long carId, String newBrand) {
        Optional<Car> findCar3 = cars.stream().filter(car -> car.getCarId() == carId).findFirst();
        if (findCar3.isPresent()) {
            Car carBrand = findCar3.get();
            carBrand.setBrand(newBrand);
            return true;
        }
        return false;
    }


    @Override
    public boolean removeById(Long carId) {
        Car find4 = cars.stream().filter(car -> car.getCarId() == carId).findFirst().get();
        if (cars.remove(find4)) {
        return true;
    }
        return false;

        }
        public long findMaxId() {
            Car car = Collections.max(cars, Comparator.comparing(Car::getCarId));
            return car.getCarId()+1;
    }
}
