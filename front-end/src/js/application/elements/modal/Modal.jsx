import React from 'react';
import ModalHeader from './ModalHeader.jsx';
import ModalContent from './ModalContent.jsx';
import ModalFooter from './ModalFooter.jsx';

export default class Modal extends React.Component {

    constructor(props) {
        super(props);
        this.clickOutside = this.clickOutside.bind(this);
        this.keyDown = this.keyDown.bind(this);
    }

    clickOutside(event){
        if(event.target.className === 'modal'){
            this.context.functions.showAdd();
        }
    }

    keyDown(event){
        switch(event.keyCode){
            case 27: //esc
                this.context.functions.showAdd();
                break;

            case 13: // return
                this.props.finish();
                break;
        }
    }

    render() {
        let modalName = this.props.header;
        let close = this.context.functions.showAdd;
        let headerHandlers = {
            close: close
        };
        let footerHandlers = {
            apply: this.props.finish
        };
        return <div className="modal" onClick={this.clickOutside} onKeyDown={this.keyDown}>
            <div className="modal-context">
                <ModalHeader handlers={headerHandlers}>
                    <div className="modal-title">
                        {modalName}
                    </div>
                </ModalHeader>
                <ModalContent>
                    {this.props.children}
                </ModalContent>
                <ModalFooter handlers={footerHandlers}/>
            </div>
        </div>
    }
}

Modal.contextTypes = {
    streamers: React.PropTypes.array.isRequired,
    functions: React.PropTypes.object.isRequired
};

