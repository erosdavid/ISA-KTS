import { Driver, User, Vehicle } from 'app/model/User';
import { getRequest, postRequest, putRequest } from '../service/base.service';
import { RegisterUser } from 'app/service/auth.service';
import { DriverLocation, DriverStatus } from 'app/model/Location';

const CONTROLLER = 'user';

export const getUsers = async () =>
  getRequest(CONTROLLER)
    .then((response) => response.data as User[])
    .catch(() => null);

export const getUser = async (id: number) =>
  getRequest(`${CONTROLLER}/${id}`)
    .then((response) => response.data as User)
    .catch(() => null);

export const getDriver = async (id: number) =>
  getRequest(`${CONTROLLER}/driver/${id}`)
    .then((response) => response.data as Driver)
    .catch(() => null);

export const getUserInfo = async () =>
  getRequest(`${CONTROLLER}/info`)
    .then((response) => response.data as User)
    .catch(() => null);

export type UpdateUser = Omit<RegisterUser, 'username'>;
export const updateUser = async (id: number, data: UpdateUser) =>
  putRequest(`${CONTROLLER}/${id}`, data).then((response) => response.data as User);

export const updateUserProfile = async (data: UpdateUser) =>
  putRequest(`${CONTROLLER}/profile`, data).then((response) => response.data as User);

type CreateVehicle = Omit<Vehicle, 'id' | 'vehicleType'> & { vehicleTypeId: number };
type CreateDriver = RegisterUser & {
  driverLicense: string;
  vehicle: CreateVehicle;
};
export const createDriver = async (data: CreateDriver) =>
  postRequest(`${CONTROLLER}/driver`, data).then((response) => response.data as Driver);

export const getActiveDriversLocations = async () =>
  getRequest(`${CONTROLLER}/driver/location`)
    .then((response) => response.data as DriverLocation[])
    .catch(() => []);

export const getIsCurrentDriverActive = async () =>
  getRequest(`${CONTROLLER}/driver/status`).then((response) => response.data as DriverStatus);

export const activateCurrentDriver = async () => putRequest(`${CONTROLLER}/driver/activate`);

export const deactivateCurrentDriver = async () => putRequest(`${CONTROLLER}/driver/deactivate`);
