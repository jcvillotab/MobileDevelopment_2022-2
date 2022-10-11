import 'dart:ffi';

import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:location/location.dart';

void main() {
  runApp(const MainView());
}

class MainView extends StatelessWidget {
  const MainView({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'GPS App',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: MapView(),
    );
  }
}

class MapView extends StatefulWidget {
  const MapView({super.key});

  @override
  State<MapView> createState() => _MapViewState();
}

class _MapViewState extends State<MapView> {
  late GoogleMapController mapController;
  Location location = Location();

  _MapViewState() {
    _initLocationService();
  }
  @override
  Widget build(BuildContext context) {
    var media = MediaQuery.of(context);

    return Scaffold(
      appBar: AppBar(
        title: const Text('GPS App'),
      ),
      body: Container(
        height: media.size.height,
        width: media.size.width,
        child: GoogleMap(
          mapType: MapType.normal,
          initialCameraPosition: const CameraPosition(
            target: LatLng(4.624335, -74.063644), // Bogotá
            zoom: 15,
          ),
          onMapCreated: CreateMap,
          myLocationEnabled: true,
          zoomControlsEnabled: false,
          myLocationButtonEnabled: false,
        ),
      ),
      floatingActionButton:
          Column(mainAxisAlignment: MainAxisAlignment.end, children: [
        FloatingActionButton(onPressed: zoomIn, child: const Icon(Icons.add)),
        SizedBox(height: media.size.height * 0.02),
        FloatingActionButton(
            onPressed: zoomOut, child: const Icon(Icons.remove)),
        SizedBox(height: media.size.height * 0.02),
        FloatingActionButton(
            onPressed: centerCamera,
            child: const Icon(Icons.center_focus_strong)),
      ]),
    );
  }

  void CreateMap(GoogleMapController controller) {
    mapController = controller;
  }

  void _initLocationService() async {
    if (!await location.serviceEnabled()) {
      if (!await location.requestService()) {
        return;
      }
    }

    if (await location.hasPermission() == PermissionStatus.denied) {
      if (await location.requestPermission() != PermissionStatus.granted) {
        return;
      }
    }
  }

  void centerCamera() async {
    final LatLng currentPosition = LatLng(
      (await location.getLocation()).latitude!,
      (await location.getLocation()).longitude!,
    );
    mapController.animateCamera(CameraUpdate.newCameraPosition(
      CameraPosition(
        target: currentPosition,
        zoom: 17.0,
      ),
    ));
  }

  void zoomIn() {
    mapController.animateCamera(CameraUpdate.zoomIn());
  }

  void zoomOut() {
    mapController.animateCamera(CameraUpdate.zoomOut());
  }
}
