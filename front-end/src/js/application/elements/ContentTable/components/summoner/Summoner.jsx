import React from 'react';
import StandardSummonerView from './StandardSummonerView.jsx';
import SummonerEditView from './SummonerEditView.jsx';
import  ButtonGroup from '../ButtonGroup.jsx';

export default class Summoner extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            edited: false,
            played: false
        };
        this.handleEditName = this.handleEditName.bind(this);
        this.handleChangeEditState = this.handleChangeEditState.bind(this);
        this.handleEditServer = this.handleEditServer.bind(this);
        this.handleRemove = this.handleRemove.bind(this);
        this.getStandardButtons = this.getStandardButtons.bind(this);
        this.getEditButtons = this.getEditButtons.bind(this);
    }

    componentWillReceiveProps(nextProps) {
        let online = nextProps.summoner.online;
        if(online && !this.state.played) {
            this.context.functions.playSound("summoner");
            this.setState({played: true});
        } else if (!online && this.state.played){
            this.setState({played: false});
        }
    }

    getStandardButtons() {
        return [
            {
                name: "Edit",
                aClass: "edit-button",
                action: this.handleChangeEditState
            },
            {
                name: "Remove",
                aClass: "remove-button",
                action: this.handleRemove
            }
        ];
    }

    getEditButtons() {
        return [
            {
                name: "Apply",
                aClass: "apply-button",
                action: this.handleChangeEditState
            },
            {
                name: "Remove",
                aClass: "remove-button",
                action: this.handleRemove
            }
        ];
    }

    handleEditName(event) {
        this.props.summoner.name = event.target.value;
        this.setState(this.state);
    }

    handleEditServer(event) {
        this.props.summoner.server = event.target.value;
        this.setState(this.state);
    }

    handleRemove() {
        this.props.removeHandler(this.props.summoner);
    }

    handleChangeEditState() {
        this.setState({edited: !this.state.edited});
    }

    render() {
        let name = this.props.summoner.name;
        let server = this.props.summoner.server;
        let online = (this.props.summoner.online) ? " online" : "";
        let champion = this.props.summoner.champion;
        let editHandlers = {
            name: this.handleEditName,
            server: this.handleEditServer,
            editState: this.handleChangeEditState
        };
        let buttons = (this.state.edited) ? this.getEditButtons() : this.getStandardButtons();
        return <div className={"row summoner" + online}>
            {(this.state.edited) ?
                <SummonerEditView name={name} online={online} server={server} handlers={editHandlers}/> :
                <StandardSummonerView name={name} online={online} server={server} champion={champion}/>}
            <ButtonGroup buttons={buttons}/>
        </div>
    }
}

Summoner.contextTypes = {
    functions: React.PropTypes.object.isRequired
};
Summoner.defaultProps = {
    summoner: {
        name: '',
        server: 'EUNE'
    }
};

Summoner.propTypes = {
    summoner: React.PropTypes.object.isRequired,
    removeHandler: React.PropTypes.func.isRequired
};
