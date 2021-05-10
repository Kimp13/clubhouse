package com.example.presentation.ui.interfaces

interface FragmentGateway :
    AccessLocationPermissionRequester,
    ContactCardClickListener,
    ContactLocationRetriever,
    ContactLocationViewer,
    PoppableBackStackOwner,
    ReadContactsPermissionRequester,
    RequestPermissionDialogDismissListener,
    ContactLocationNavigator
