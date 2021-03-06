// Remember to run `npx webpack --mode=development` everytime editing this file

import React from 'react';
import ReactDOM from 'react-dom';
window["React"] = React;
// window["cljsjs.react"] = React;
window["ReactDOM"] = ReactDOM;
// window["cljsjs.react.dom"] = ReactDOM;
// window["cljsjs.react-dom"] = ReactDOM;

import GoogleCharts from 'google-charts';
window["GoogleCharts"] = GoogleCharts;

import * as firebase from 'firebase/app';
import 'firebase/auth';
import 'firebase/firestore';
window["firebase"] = firebase;

import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import ExpandLessIcon from '@material-ui/icons/ExpandLess';
window["ExpandMore"] = ExpandMoreIcon;
window["ExpandLess"] = ExpandLessIcon;
import ChevronLeftIcon from '@material-ui/icons/ChevronLeft';
import ChevronRightIcon from '@material-ui/icons/ChevronRight';
window["ChevronLeft"] = ChevronLeftIcon;
window["ChevronRight"] = ChevronRightIcon;
import SearchIcon from '@material-ui/icons/Search';
window["Search"] = SearchIcon;

import Button from '@material-ui/core/Button';
window["Button"] = Button;
import Checkbox from '@material-ui/core/Checkbox';
window["Checkbox"] = Checkbox;
import FormControlLabel from '@material-ui/core/FormControlLabel';
window["FormControlLabel"] = FormControlLabel;
import MenuItem from '@material-ui/core/MenuItem';
window["MenuItem"] = MenuItem;
import Select from '@material-ui/core/Select';
window["Select"] = Select;
import Slider from '@material-ui/core/Slider';
window["Slider"] = Slider;
import '@vaadin/vaadin-date-picker/vaadin-date-picker.js';
// import AppBar from '@material-ui/core/AppBar';
// window["AppBar"] = AppBar;
// import Avatar from '@material-ui/core/Avatar';
// window["Avatar"] = Avatar;
// import Backdrop from '@material-ui/core/Backdrop';
// window["Backdrop"] = Backdrop;
// import Badge from '@material-ui/core/Badge';
// window["Badge"] = Badge;
// import BottomNavigation from '@material-ui/core/BottomNavigation';
// window["BottomNavigation"] = BottomNavigation;
// import BottomNavigationAction from '@material-ui/core/BottomNavigationAction';
// window["BottomNavigationAction"] = BottomNavigationAction;
// import Box from '@material-ui/core/Box';
// window["Box"] = Box;
// import Breadcrumbs from '@material-ui/core/Breadcrumbs';
// window["Breadcrumbs"] = Breadcrumbs;
// import ButtonBase from '@material-ui/core/ButtonBase';
// window["ButtonBase"] = ButtonBase;
// import ButtonGroup from '@material-ui/core/ButtonGroup';
// window["ButtonGroup"] = ButtonGroup;
// import Card from '@material-ui/core/Card';
// window["Card"] = Card;
// import CardActionArea from '@material-ui/core/CardActionArea';
// window["CardActionArea"] = CardActionArea;
// import CardActions from '@material-ui/core/CardActions';
// window["CardActions"] = CardActions;
// import CardContent from '@material-ui/core/CardContent';
// window["CardContent"] = CardContent;
// import CardHeader from '@material-ui/core/CardHeader';
// window["CardHeader"] = CardHeader;
// import CardMedia from '@material-ui/core/CardMedia';
// window["CardMedia"] = CardMedia;
// import Chip from '@material-ui/core/Chip';
// window["Chip"] = Chip;
// import CircularProgress from '@material-ui/core/CircularProgress';
// window["CircularProgress"] = CircularProgress;
// import ClickAwayListener from '@material-ui/core/ClickAwayListener';
// window["ClickAwayListener"] = ClickAwayListener;
// import Collapse from '@material-ui/core/Collapse';
// window["Collapse"] = Collapse;
// import Container from '@material-ui/core/Container';
// window["Container"] = Container;
// import CssBaseline from '@material-ui/core/CssBaseline';
// window["CssBaseline"] = CssBaseline;
// import Dialog from '@material-ui/core/Dialog';
// window["Dialog"] = Dialog;
// import DialogActions from '@material-ui/core/DialogActions';
// window["DialogActions"] = DialogActions;
// import DialogContent from '@material-ui/core/DialogContent';
// window["DialogContent"] = DialogContent;
// import DialogContentText from '@material-ui/core/DialogContentText';
// window["DialogContentText"] = DialogContentText;
// import DialogTitle from '@material-ui/core/DialogTitle';
// window["DialogTitle"] = DialogTitle;
// import Divider from '@material-ui/core/Divider';
// window["Divider"] = Divider;
// import Drawer from '@material-ui/core/Drawer';
// window["Drawer"] = Drawer;
// import ExpansionPanel from '@material-ui/core/ExpansionPanel';
// window["ExpansionPanel"] = ExpansionPanel;
// import ExpansionPanelActions from '@material-ui/core/ExpansionPanelActions';
// window["ExpansionPanelActions"] = ExpansionPanelActions;
// import ExpansionPanelDetails from '@material-ui/core/ExpansionPanelDetails';
// window["ExpansionPanelDetails"] = ExpansionPanelDetails;
// import ExpansionPanelSummary from '@material-ui/core/ExpansionPanelSummary';
// window["ExpansionPanelSummary"] = ExpansionPanelSummary;
// import Fab from '@material-ui/core/Fab';
// window["Fab"] = Fab;
// import Fade from '@material-ui/core/Fade';
// window["Fade"] = Fade;
// import FilledInput from '@material-ui/core/FilledInput';
// window["FilledInput"] = FilledInput;
// import FormControl from '@material-ui/core/FormControl';
// window["FormControl"] = FormControl;
// import FormGroup from '@material-ui/core/FormGroup';
// window["FormGroup"] = FormGroup;
// import FormHelperText from '@material-ui/core/FormHelperText';
// window["FormHelperText"] = FormHelperText;
// import FormLabel from '@material-ui/core/FormLabel';
// window["FormLabel"] = FormLabel;
// import Grid from '@material-ui/core/Grid';
// window["Grid"] = Grid;
// import GridList from '@material-ui/core/GridList';
// window["GridList"] = GridList;
// import GridListTile from '@material-ui/core/GridListTile';
// window["GridListTile"] = GridListTile;
// import GridListTileBar from '@material-ui/core/GridListTileBar';
// window["GridListTileBar"] = GridListTileBar;
// import Grow from '@material-ui/core/Grow';
// window["Grow"] = Grow;
// import Hidden from '@material-ui/core/Hidden';
// window["Hidden"] = Hidden;
// import Icon from '@material-ui/core/Icon';
// window["Icon"] = Icon;
// import IconButton from '@material-ui/core/IconButton';
// window["IconButton"] = IconButton;
// import Input from '@material-ui/core/Input';
// window["Input"] = Input;
// import InputAdornment from '@material-ui/core/InputAdornment';
// window["InputAdornment"] = InputAdornment;
// import InputBase from '@material-ui/core/InputBase';
// window["InputBase"] = InputBase;
// import InputLabel from '@material-ui/core/InputLabel';
// window["InputLabel"] = InputLabel;
// import LinearProgress from '@material-ui/core/LinearProgress';
// window["LinearProgress"] = LinearProgress;
// import Link from '@material-ui/core/Link';
// window["Link"] = Link;
// import List from '@material-ui/core/List';
// window["List"] = List;
// import ListItem from '@material-ui/core/ListItem';
// window["ListItem"] = ListItem;
// import ListItemAvatar from '@material-ui/core/ListItemAvatar';
// window["ListItemAvatar"] = ListItemAvatar;
// import ListItemIcon from '@material-ui/core/ListItemIcon';
// window["ListItemIcon"] = ListItemIcon;
// import ListItemSecondaryAction from '@material-ui/core/ListItemSecondaryAction';
// window["ListItemSecondaryAction"] = ListItemSecondaryAction;
// import ListItemText from '@material-ui/core/ListItemText';
// window["ListItemText"] = ListItemText;
// import ListSubheader from '@material-ui/core/ListSubheader';
// window["ListSubheader"] = ListSubheader;
// import Menu from '@material-ui/core/Menu';
// window["Menu"] = Menu;
// import MenuList from '@material-ui/core/MenuList';
// window["MenuList"] = MenuList;
// import MobileStepper from '@material-ui/core/MobileStepper';
// window["MobileStepper"] = MobileStepper;
// import Modal from '@material-ui/core/Modal';
// window["Modal"] = Modal;
// import NativeSelect from '@material-ui/core/NativeSelect';
// window["NativeSelect"] = NativeSelect;
// import NoSsr from '@material-ui/core/NoSsr';
// window["NoSsr"] = NoSsr;
// import OutlinedInput from '@material-ui/core/OutlinedInput';
// window["OutlinedInput"] = OutlinedInput;
// import Paper from '@material-ui/core/Paper';
// window["Paper"] = Paper;
// import Popover from '@material-ui/core/Popover';
// window["Popover"] = Popover;
// import Popper from '@material-ui/core/Popper';
// window["Popper"] = Popper;
// import Portal from '@material-ui/core/Portal';
// window["Portal"] = Portal;
// import Radio from '@material-ui/core/Radio';
// window["Radio"] = Radio;
// import RadioGroup from '@material-ui/core/RadioGroup';
// window["RadioGroup"] = RadioGroup;
// import RootRef from '@material-ui/core/RootRef';
// window["RootRef"] = RootRef;
// import Slide from '@material-ui/core/Slide';
// window["Slide"] = Slide;
// import Snackbar from '@material-ui/core/Snackbar';
// window["Snackbar"] = Snackbar;
// import SnackbarContent from '@material-ui/core/SnackbarContent';
// window["SnackbarContent"] = SnackbarContent;
// import Step from '@material-ui/core/Step';
// window["Step"] = Step;
// import StepButton from '@material-ui/core/StepButton';
// window["StepButton"] = StepButton;
// import StepConnector from '@material-ui/core/StepConnector';
// window["StepConnector"] = StepConnector;
// import StepContent from '@material-ui/core/StepContent';
// window["StepContent"] = StepContent;
// import StepIcon from '@material-ui/core/StepIcon';
// window["StepIcon"] = StepIcon;
// import StepLabel from '@material-ui/core/StepLabel';
// window["StepLabel"] = StepLabel;
// import Stepper from '@material-ui/core/Stepper';
// window["Stepper"] = Stepper;
// import SvgIcon from '@material-ui/core/SvgIcon';
// window["SvgIcon"] = SvgIcon;
// import SwipeableDrawer from '@material-ui/core/SwipeableDrawer';
// window["SwipeableDrawer"] = SwipeableDrawer;
// import Switch from '@material-ui/core/Switch';
// window["Switch"] = Switch;
// import Tab from '@material-ui/core/Tab';
// window["Tab"] = Tab;
// import Table from '@material-ui/core/Table';
// window["Table"] = Table;
// import TableBody from '@material-ui/core/TableBody';
// window["TableBody"] = TableBody;
// import TableCell from '@material-ui/core/TableCell';
// window["TableCell"] = TableCell;
// import TableFooter from '@material-ui/core/TableFooter';
// window["TableFooter"] = TableFooter;
// import TableHead from '@material-ui/core/TableHead';
// window["TableHead"] = TableHead;
// import TablePagination from '@material-ui/core/TablePagination';
// window["TablePagination"] = TablePagination;
// import TableRow from '@material-ui/core/TableRow';
// window["TableRow"] = TableRow;
// import TableSortLabel from '@material-ui/core/TableSortLabel';
// window["TableSortLabel"] = TableSortLabel;
// import Tabs from '@material-ui/core/Tabs';
// window["Tabs"] = Tabs;
// import TextareaAutosize from '@material-ui/core/TextareaAutosize';
// window["TextareaAutosize"] = TextareaAutosize;
// import TextField from '@material-ui/core/TextField';
// window["TextField"] = TextField;
// import Toolbar from '@material-ui/core/Toolbar';
// window["Toolbar"] = Toolbar;
// import Tooltip from '@material-ui/core/Tooltip';
// window["Tooltip"] = Tooltip;
// import Typography from '@material-ui/core/Typography';
// window["Typography"] = Typography;
// import Zoom from '@material-ui/core/Zoom';
// window["Zoom"] = Zoom;
