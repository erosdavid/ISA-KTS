import { useEffect, useState } from 'react';
import { LatLngTuple, geoJson, GeoJSON, icon, marker, Marker, Point, Icon } from 'leaflet';
import { useMap } from 'react-leaflet';

import { useAppSelector } from 'app/hooks/common';
import { selectRoutes, selectSelectedRouteId } from 'app/pages/routes/routes-page.slice';

import { selectActiveDriversLocations } from 'app/pages/common.slice';
import { useDriverStatusContext } from 'app/contexts/driver-status/driver-status-provider';
import { useAuthContext } from 'app/contexts/auth/auth-context-provider';
import { useActiveRideContext } from 'app/contexts/active-ride/active-ride-provider';

import redIcon from 'assets/car-red.png';
import greenIcon from 'assets/car-green.png';
import exclamationIcon from 'assets/exclamation-mark.png';
import iconUrl from 'leaflet/dist/images/marker-icon.png';
import shadowUrl from 'leaflet/dist/images/marker-shadow.png';

const redCarIcon = icon({
  iconUrl: redIcon,
  iconSize: [25, 25]
});

const greenCarIcon = icon({
  iconUrl: greenIcon,
  iconSize: [25, 25]
});

const locationIcon = icon({
  iconUrl,
  shadowUrl
});

const exclamationMarkIcon = icon({
  iconUrl: exclamationIcon,
  iconSize: [25, 25]
});

const selectedRouteStyle = {
  color: 'blue',
  opacity: 0.8
};

const alternativeRouteStyle = {
  color: 'gray',
  opacity: 0.8
};

type GeoJSONRoutes = { [key: string | number]: GeoJSON };

export const MainMapContent = () => {
  const map = useMap();
  const { driverWithPanicInCar } = useActiveRideContext();

  const { active: isDriverActive } = useDriverStatusContext();
  const { user } = useAuthContext();
  const routes = useAppSelector(selectRoutes);
  const selectedRouteId = useAppSelector(selectSelectedRouteId);
  const locations = useAppSelector(selectActiveDriversLocations);

  const [displayedRoutes, setDisplayedRoutes] = useState<GeoJSONRoutes>({});

  useEffect(() => {
    if (routes.bbox?.length) {
      const bounds: LatLngTuple[] = [
        [routes.bbox[1], routes.bbox[0]],
        [routes.bbox[3], routes.bbox[2]]
      ];
      map.fitBounds(bounds);
    }
  }, [map, routes]);

  useEffect(() => {
    const gJSONsMap = routes.features?.reduce((store, current) => {
      if (!current.id) return store;
      store[current.id] = geoJson(current.geometry);
      return store;
    }, {} as GeoJSONRoutes);
    if (!gJSONsMap) return;

    setDisplayedRoutes(gJSONsMap);

    Object.values(gJSONsMap).forEach((gJSON) => map.addLayer(gJSON));

    return () => {
      Object.values(gJSONsMap).forEach((gJSON) => map.removeLayer(gJSON));
      setDisplayedRoutes({});
    };
  }, [map, routes.features]);

  useEffect(() => {
    Object.entries(displayedRoutes).forEach(([key, gJSON]) => {
      if (parseInt(key) === selectedRouteId) gJSON.setStyle(selectedRouteStyle).bringToFront();
      else gJSON.setStyle(alternativeRouteStyle);
    });
  }, [displayedRoutes, selectedRouteId]);

  useEffect(() => {
    const markers: Marker[] = locations.map(({ longitude, latitude, occupied, id }) => {
      const carIcon = occupied ? redCarIcon : greenCarIcon;
      const locationCarIcon = isDriverActive && id === user?.id ? locationIcon : carIcon;

      const icon = id === driverWithPanicInCar ? exclamationMarkIcon : locationCarIcon;
      const newMarker = marker([latitude, longitude], { icon });
      return newMarker;
    });
    markers.forEach((m) => m.addTo(map));

    return () => {
      markers.forEach((m) => m.remove());
    };
  }, [map, locations, isDriverActive, user?.id, driverWithPanicInCar]);

  return <></>;
};
