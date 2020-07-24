export class Vehicle {
  id: string;
  name: string;
  lat: number;
  lng: number;
  dateAndTime: string;
  speed: number;

  constructor(id: string, name: string, lat:number, lng:number, dateAndTime:string, speed: number) {
    this.id = id;
    this.name = name;
    this.lat = lat;
    this.lng = lng;
    this.dateAndTime = dateAndTime;
    this.speed = speed;
  }

}
